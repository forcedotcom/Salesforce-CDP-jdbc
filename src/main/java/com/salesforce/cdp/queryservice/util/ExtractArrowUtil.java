package com.salesforce.cdp.queryservice.util;

import com.salesforce.cdp.queryservice.core.QueryServiceResultSetMetaData;
import lombok.extern.slf4j.Slf4j;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.FieldVector;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.ipc.ArrowStreamReader;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains the utilities for processing the arrow stream.
 */
@Slf4j
public class ExtractArrowUtil extends ArrowUtil {

  private ArrowStreamReader arrowStreamReader;
  private RootAllocator streamRootAllocator;
  private VectorSchemaRoot vectorSchemaRoot;

  public ExtractArrowUtil(InputStream inputStream) throws SQLException {
    super();
    initialiseArrowReaderForStream(inputStream);
    try {
      vectorSchemaRoot = arrowStreamReader.getVectorSchemaRoot();
    } catch (IOException e) {
      throw new SQLException("Error while getting VectorSchemaRoot");
    }
  }

  public void initialiseArrowReaderForStream(InputStream inputStream){
    if(arrowStreamReader == null){
      streamRootAllocator = new RootAllocator(Long.MAX_VALUE);
      arrowStreamReader =new ArrowStreamReader(inputStream, streamRootAllocator);
    }
  }

  public List<Object> getRowsFromStreamResponse() throws SQLException {
    if (arrowStreamReader == null) {
      throw new SQLException("Arrow Reader not created for Stream");
    }
    List<FieldVector> fieldVectors = null;
    List<Object> data = new ArrayList<>();
    try{
      fieldVectors = vectorSchemaRoot.getFieldVectors();
      if (arrowStreamReader.loadNextBatch()) {
        int rowCount = fieldVectors.get(0).getValueCount();
        for(int i=0; i<rowCount; ++i) {
          List<Object> row = new ArrayList<>();
          for(FieldVector fieldVector : fieldVectors) {
            Object fieldValue = this.getFieldValue(fieldVector, i);
            row.add(fieldValue);
          }
          data.add(row);
        }
      }}catch(Exception e){
      log.error("Encountered exception", e);
      closeReader();
    }
    return data;
  }

  public void closeReader() {
    List<FieldVector> vectors =  vectorSchemaRoot.getFieldVectors();
    if(vectors != null){
      for(FieldVector fieldVector : vectors) {
        if(fieldVector !=  null) {
          fieldVector.close();
        }
      }
      vectors = null;
    }
    if(streamRootAllocator != null) {
      streamRootAllocator.close();
      streamRootAllocator = null;
    }
  }
}