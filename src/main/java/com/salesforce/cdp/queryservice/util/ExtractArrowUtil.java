package com.salesforce.cdp.queryservice.util;

import com.google.protobuf.ByteString;
import com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryStreamResponse;
import com.salesforce.cdp.queryservice.core.QueryServiceResultSetMetaData;
import lombok.extern.slf4j.Slf4j;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.FieldVector;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.ipc.ArrowStreamReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * This class contains the utilities for processing the arrow stream.
 */
@Slf4j
public class ExtractArrowUtil extends ArrowUtil {


    private Iterator<AnsiSqlQueryStreamResponse> inputStream;
    private RootAllocator streamRootAllocator;

    public ExtractArrowUtil(Iterator<AnsiSqlQueryStreamResponse> inputStream) {
        super();
        this.inputStream = inputStream;
        streamRootAllocator = new RootAllocator(Long.MAX_VALUE);
    }

    public boolean isNextChunkPresent() {
        return inputStream.hasNext();
    }

    public List<Object> getRowsFromStreamResponse() throws SQLException {
        AnsiSqlQueryStreamResponse response = inputStream.next();
        ByteString arrowResponseChunk = response.getArrowResponseChunk().getData();
        InputStream chunkInputStream = new ByteArrayInputStream(arrowResponseChunk.toByteArray());
        ArrowStreamReader arrowStreamReader = new ArrowStreamReader(chunkInputStream, streamRootAllocator);

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
    }

    public void closeReader() {
        if (streamRootAllocator != null) {
            streamRootAllocator.close();
            streamRootAllocator = null;
        }
    }
}