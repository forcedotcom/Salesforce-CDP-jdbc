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
import com.salesforce.cdp.queryservice.util.ExtractArrowUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class QueryServiceHyperResultSet extends QueryServiceResultSet {
    private final ExtractArrowUtil arrowUtil;
    private final Iterator<AnsiSqlQueryStreamResponse> responseIterator;

    protected List<Object> data;
    protected int currentPageNum = 0;

    public QueryServiceHyperResultSet(Iterator<AnsiSqlQueryStreamResponse> responseIterator,
                                      ResultSetMetaData resultSetMetaData,
                                      QueryServiceAbstractStatement statement) throws SQLException {
        this.data = data == null ? new ArrayList<>(): data;
        this.responseIterator = responseIterator;
        this.resultSetMetaData = resultSetMetaData;
        this.statement = statement;
        this.arrowUtil = new ExtractArrowUtil(responseIterator);
    }

    @Override
    public boolean next() throws SQLException {
        errorOutIfClosed();

        if (currentRow == -1 && isNextChunkPresent()) {
            getNextChunk();
        } else {
            currentRow++;
        }

        if (data != null && currentRow < data.size()) {
            return true;
        }

        if (isNextChunkPresent()) {
            getNextChunk();
            if (data != null && data.size() > 0) {
                return true;
            }
        }

        // Closing as this is move forward only cursor.
        log.info("Resultset {} does not have any more rows. Total {} pages retrieved", this, currentPageNum);
        return false;
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
            throw new SQLException(e.getMessage());
        }
    }

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
        return ((ArrayList) row).get(columnIndex);
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
            if (rows != null) {
                this.data = rows;
                currentRow = 0;
            }
        } catch (Exception e) {
            log.error("Error while getting the data chunk {}", this, e);
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

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        errorOutIfClosed();
        Object value = getObject(columnLabel);
        if (wasNull() || value== null || StringUtils.EMPTY.equals(value)) {
            wasNull.set(true);
            return null;
        }

        if(value instanceof LocalDateTime) {
            long epoch = ((LocalDateTime) value).toEpochSecond(ZoneOffset.UTC);
            return new Date(epoch * 1000);
        }
        if(value instanceof Long) {
            return new Date((Long)value);
        }
        if(value instanceof Date) {
            return (Date) value;
        } else {
            throw new SQLException("Invalid date from server: " + value + ", columnLabel: " + columnLabel);
        }
    }

    private boolean isNextChunkPresent() throws SQLException {
        try {
            return arrowUtil.isNextChunkPresent();
        } catch (Exception e) {
            log.error("Exception while fetching next chunk ", e);
            throw new SQLException(e.getMessage());
        }
    }
}
