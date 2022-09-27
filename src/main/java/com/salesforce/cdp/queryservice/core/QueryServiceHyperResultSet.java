/*
 * Copyright (c) 2021, salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.salesforce.cdp.queryservice.core;

import com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryStreamResponse;
import com.salesforce.cdp.queryservice.util.AnsiSqlQueryStreamResponseDataStream;
import com.salesforce.cdp.queryservice.util.ExtractArrowUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class QueryServiceHyperResultSet extends QueryServiceResultSet {
    private final ExtractArrowUtil arrowUtil;
    private final Iterator<AnsiSqlQueryStreamResponse> responseIterator;

    protected List<Object> data;
    protected int currentPageNum = 0;
    private AnsiSqlQueryStreamResponseDataStream dataStream;

    public QueryServiceHyperResultSet(Iterator<AnsiSqlQueryStreamResponse> responseIterator,
                                      ResultSetMetaData resultSetMetaData,
                                      QueryServiceAbstractStatement statement) throws SQLException {
        this.data = data == null ? new ArrayList<>(): data;
        this.responseIterator = responseIterator;
        this.resultSetMetaData = resultSetMetaData;
        this.statement = statement;
        this.dataStream = new AnsiSqlQueryStreamResponseDataStream(responseIterator);
        this.arrowUtil = new ExtractArrowUtil(this.dataStream);
    }

    @Override
    public boolean next() throws SQLException {
        try {
            errorOutIfClosed();

            if (currentRow == -1 && isNextChunkPresent()) {
                getNextChunk();
            } else {
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

            // datastream and reader should be closed here.
            arrowUtil.closeReader();
            closeDataStream();

            // Closing as this is move forward only cursor.
            log.info("Resultset {} does not have any more rows. Total {} pages retrieved", this, currentPageNum);
            return false;
        } catch (SQLException e) {
            closeDataStream();
            throw e;
        }
    }

    private void closeDataStream() {
        if (dataStream != null) {
            try {
                dataStream.close();
            } catch (IOException ex) {
                log.error("Encountered exception while closing datastream ", ex);
            }
            dataStream = null;
        }
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        errorOutIfClosed();
        Object value = getObject(columnLabel);
        if (wasNull() || StringUtils.isBlank(value.toString())) {
            wasNull.set(true);
            return 0L;
        }

        if (value instanceof Double) {
            return ((Double) value).longValue();
        }
        return Long.parseLong(value.toString());
    }

    /**
     * ColumnIndex starts from 1.
     */
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return this.resultSetMetaData;
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        errorOutIfClosed();
        try {
            Object value = getValue(data.get(currentRow), columnIndex-1);
            wasNull.set(value == null);
            return value;
        } catch (SQLException e) {
            closeDataStream();
            throw new SQLException(e.getMessage());
        }
    }

//    @Override
//    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
//        errorOutIfClosed();
//        String value = getString(columnLabel);
//        if (StringUtils.isBlank(value)) {
//            wasNull.set(true);
//            // TODO: test this
//            return BigDecimal.ZERO;
//        }
//        return new BigDecimal(value);
//    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        errorOutIfClosed();
        int columnIndex = getColumnIndexByName(columnLabel);
        return getObject(columnIndex+1);
    }

    @Override
    protected Object getValue(Object row, String columnLabel) throws SQLException {
        errorOutIfClosed();
        int columnIndex = getColumnIndexByName(columnLabel);
        return getValue(row, columnIndex);
    }

    private Object getValue(Object row, int columnIndex) throws SQLException {
        return ((ArrayList)row).get(columnIndex);
    }

    private int getColumnIndexByName(String columnName) throws SQLException {
        return ((QueryServiceResultSetMetaData) resultSetMetaData)
            .getColumnNameToPosition()
            .get(columnName);
    }

    private void getNextChunk() throws SQLException {
        log.trace("Fetching page with number {} for resultset {}", ++currentPageNum, this);
        try {
            List<Object> rows = arrowUtil.getRowsFromStreamResponse();
            if (rows != null && rows.size() > 0) {
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
            return dataStream.hasNext();
        } catch (Exception e) {
            log.error("Exception while fetching next chunk ", e);
            throw new SQLException(e.getMessage());
        }
    }
}
