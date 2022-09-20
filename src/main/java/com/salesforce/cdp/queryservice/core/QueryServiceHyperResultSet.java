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

import com.google.protobuf.ListValue;
import com.google.protobuf.Value;
import com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryStreamResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class QueryServiceHyperResultSet extends QueryServiceResultSet {

    protected List<ListValue> data;
    private Iterator<AnsiSqlQueryStreamResponse> responseIterator;
    protected int currentPageNum = 0;

    public QueryServiceHyperResultSet(Iterator<AnsiSqlQueryStreamResponse> responseIterator,
                                      ResultSetMetaData resultSetMetaData,
                                      QueryServiceAbstractStatement statement) {
        this.data = data == null ? new ArrayList<>(): data;
        this.responseIterator = responseIterator;
        this.resultSetMetaData = resultSetMetaData;
        this.statement = statement;
    }

    @Override
    public boolean next() throws SQLException {
        errorOutIfClosed();

        if(currentRow == -1 && isNextChunkPresent()) {
            getNextChunk();
        } else {
            currentRow++;
        }

        if (currentRow < data.size()) {
            return true;
        }

        if(isNextChunkPresent()) {
            getNextChunk();
            if(data!=null && data.size()>0) {
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

        if(value instanceof Double) {
            return ((Double) value).longValue();
        }
        return Long.parseLong(value.toString());
    }

    /**
     * ColumnIndex starts from 1.
     */
    @Override
    public Object getObject(int columnIndex) throws SQLException {
        errorOutIfClosed();
        Object value = getValue(data.get(currentRow), columnIndex-1);
        wasNull.set(value == null);
        return value;
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        errorOutIfClosed();
        int columnIndex = getColumnIndexByName(columnLabel);
        return getObject(columnIndex+1);
    }

    @Override
    protected Object getValue(Object row, String columnLabel) throws SQLException {
        int columnIndex = getColumnIndexByName(columnLabel);
        return getValue(row, columnIndex);
    }

    private Object getValue(Object row, int columnIndex) throws SQLException {
        ListValue listValueRow = (ListValue) row;
        if(columnIndex >= listValueRow.getValuesCount()) {
            return null;
        }
        return valueToObject(listValueRow.getValues(columnIndex));
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
        AnsiSqlQueryStreamResponse nextChunk = null;
        try {
            nextChunk = responseIterator.next();
        } catch (Exception e) {
            log.error("Error while getting the data chunk {}", this, e);
            throw new SQLException(e.getMessage());
        }

        updateState(nextChunk);
    }

    @Override
    protected ResultSet getNextPageData() throws SQLException {
        throw new SQLException("This method is not implemented");
    }

    @Override
    protected void updateState(ResultSet resultSet) throws SQLException {
        throw new SQLException("This method is not implemented");
    }

    protected void updateState(AnsiSqlQueryStreamResponse nextChunk) throws SQLException {
        try {
            this.data = nextChunk == null ? new ArrayList<>() : nextChunk.getResponseChunk().getRowsList();
            this.currentRow = 0;
        } catch (Exception e) {
            log.error("Error while getting the data from resultset {}", this, e);
            throw new SQLException(e.getMessage());
        }
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
            return responseIterator.hasNext();
        } catch (Exception e) {
            log.error("Exception while fetching next chunk ", e);
            throw new SQLException(e.getMessage());
        }
    }
}
