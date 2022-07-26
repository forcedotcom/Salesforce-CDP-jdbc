package com.salesforce.cdp.queryservice.core;

import com.google.protobuf.ListValue;
import com.google.protobuf.Value;
import com.salesforce.a360.queryservice.grpc.v1.AnsiSqlExtractQueryResponse;
import com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryStreamResponse;
import com.salesforce.cdp.queryservice.util.ArrowUtil;
import com.salesforce.cdp.queryservice.util.RainbowDataStream;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Slf4j
public class RainbowQueryResultSet extends  QueryServiceResultSet{
    List<Object> data ;
    private RainbowDataStream dataStream;
    private Iterator<AnsiSqlExtractQueryResponse> streamIterator;
    private ArrowUtil arrowUtil;

    public RainbowQueryResultSet(Iterator<AnsiSqlExtractQueryResponse> response,  QueryServiceAbstractStatement statement) {
        streamIterator = response;
        arrowUtil = new ArrowUtil();
        dataStream = new RainbowDataStream(response);
        initialiseArrowReader();
        this.statement = statement;
    }

    private void initialiseArrowReader() {
        arrowUtil.createArrowReaderForStream(dataStream);
    }
    @Override
    public boolean next() throws SQLException {
        try {
            errorOutIfClosed();

            if (currentRow == -1 && isNextChunkPresent()) {
                getNextChunk();
            } else if(data !=null) {
                currentRow++;
            }

            if (data!=null && currentRow < data.size()) {
                return true;
            }

            if (isNextChunkPresent()) {
                getNextChunk();
                if (data != null && data.size() > 0) {
                    return true;
                }
            }
            arrowUtil.closeReader();
            closeDataStream();
            // Closing as this is move forward only cursor.
            log.info("Resultset {} does not have any more rows. Total {} pages retrieved", this, currentPageNum);
            return false;
        }
        catch (SQLException e){

            closeDataStream();
            throw e;
        }
    }

    private void closeDataStream() {
        if(dataStream != null) {
            try {
                dataStream.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            dataStream = null;
        }
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        try{
        errorOutIfClosed();
        Object value = getValue(data.get(currentRow), columnIndex);
        wasNull.set(value == null);
        return value;}
        catch(SQLException e) {
            closeDataStream();
            throw e;
        }
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        throw new SQLException("Not Implemented");
    }

    @Override
    protected Object getValue(Object row, String columnLabel) throws SQLException {
      throw new SQLException("Not Implemented");
    }

    private Object getValue(Object row, int columnIndex) throws SQLException {
        return valueToObject(((ListValue) row).getValues(columnIndex));
    }

    private static Object valueToObject(Value value) {
        switch (value.getKindCase()) {
            case NULL_VALUE:
                return null;
            case NUMBER_VALUE:
                return value.getNumberValue();
            case STRING_VALUE:
                return value.getStringValue();
            case BOOL_VALUE:
                return value.getBoolValue();
            default:
                throw new IllegalArgumentException(String.format("Unsupported protobuf value %s", value));
        }
    }

    private int getColumnIndexByName(String columnName) throws SQLException {
        return ((QueryServiceResultSetMetaData)resultSetMetaData).getColumnNameToPosition().get(columnName);
    }

    private void getNextChunk() throws SQLException {
        log.trace("Fetching page with number {} for resultset {}", ++currentPageNum, this);

        try {
           List<Object> rows = arrowUtil.getRowsFromRainbowResponse();
           if(rows != null && rows.size()>0){
              this.data = rows;
              currentRow=0;
           }
        } catch (Exception e) {
            log.error("Error while getting the data chunk {}", this, e);
            closeDataStream();
            throw new SQLException(e.getMessage());
        }
    }

    @Override
    protected ResultSet getNextPageData() throws SQLException {
        throw new SQLException("This method is not implemented");
    }

    @Override
    protected void updateState(ResultSet resultSet) throws SQLException {
        throw new SQLException("This method is not implemented");
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        return !this.isNextChunkPresent() && this.currentRow >= this.data.size();
    }

    @Override
    public boolean isLast() throws SQLException {
        return !this.isNextChunkPresent() && this.currentRow == this.data.size() - 1;
    }

    private boolean isNextChunkPresent() throws SQLException {
        try {
            return streamIterator.hasNext();
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }
}
