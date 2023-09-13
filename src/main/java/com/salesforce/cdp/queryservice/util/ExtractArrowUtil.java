package com.salesforce.cdp.queryservice.util;

import com.google.protobuf.ByteString;
import com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryStreamResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.FieldVector;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.ipc.ArrowStreamReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class contains the utilities for processing the arrow stream.
 */
@Slf4j
public class ExtractArrowUtil extends ArrowUtil {
    private Iterator<AnsiSqlQueryStreamResponse> inputStream;
    private static final long ALLOCATOR_MAX_SIZE_IN_BYTES = 100 * 1024 * 1024; // 100MB

    public ExtractArrowUtil(Iterator<AnsiSqlQueryStreamResponse> inputStream) {
        super();
        this.inputStream = inputStream;
    }

    public boolean isNextChunkPresent() {
        return inputStream.hasNext();
    }

    public List<Object> getRowsFromStreamResponse() throws SQLException {
        AnsiSqlQueryStreamResponse response = inputStream.next();
        ByteString arrowResponseChunk = response.getArrowResponseChunk().getData();
        InputStream chunkInputStream = new ByteArrayInputStream(arrowResponseChunk.toByteArray());
        RootAllocator rootAllocator = new RootAllocator(ALLOCATOR_MAX_SIZE_IN_BYTES);
        try (ArrowStreamReader arrowStreamReader = new ArrowStreamReader(chunkInputStream, rootAllocator)) {

            VectorSchemaRoot vectorSchemaRoot;

            try {
                if (!arrowStreamReader.loadNextBatch()) {
                    throw new SQLException("Unable to load the record batch");
                }
                vectorSchemaRoot = arrowStreamReader.getVectorSchemaRoot();
            } catch (IOException e) {
                throw new SQLException("Error while getting VectorSchemaRoot");
            }

            List<FieldVector> fieldVectors = vectorSchemaRoot.getFieldVectors();
            List<Object> data = new ArrayList<>();

            int rowCount = fieldVectors.get(0).getValueCount();
            for (int i = 0; i < rowCount; ++i) {
                List<Object> row = new ArrayList<>();
                for (FieldVector fieldVector : fieldVectors) {
                    Object fieldValue = this.getFieldValue(fieldVector, i);
                    row.add(fieldValue);
                }
                data.add(row);
            }

            return data;
        } catch (IOException e) {
            throw new SQLException("Failed to parse the arrow stream", e);
        } finally {
            rootAllocator.close();
        }


    }



}