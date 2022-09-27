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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class QueryServiceResultSetMetaData implements ResultSetMetaData {

    List<String> columnNames = Collections.EMPTY_LIST;
    List<String> columnTypes = Collections.EMPTY_LIST;
    List<Integer> columnTypeIds = Collections.EMPTY_LIST;
    Map<String, Integer> columnNameToPosition = new HashMap<>();

    public QueryServiceResultSetMetaData(List<String> columnNames, List<String> columnTypes, List<Integer> columnTypeIds, Map<String, Integer> columnNameToPosition) {
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
        this.columnTypeIds = columnTypeIds;
        this.columnNameToPosition = columnNameToPosition;
    }

    public QueryServiceResultSetMetaData() {
    }

    public QueryServiceResultSetMetaData(QueryServiceDbMetadata metadata) {
        this.columnNames = metadata.getColumnNames();
        this.columnTypes = metadata.getColumnTypeNames();
        this.columnTypeIds = metadata.getColumnTypes();
    }

    @Override
    public int getColumnCount() throws SQLException {
        if (columnNames == null) {
            return 0;
        }
        return columnNames.size();
    }

    public Map<String, Integer> getColumnNameToPosition() {
        return columnNameToPosition;
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        return false;
    }

    @Override
    public int isNullable(int column) throws SQLException {
        return columnNullable;
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        return false;
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        return 25;
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        if (columnNames != null) {
            return columnNames.get(column - 1);
        } else {
            return "C" + (column - 1);
        }
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        if (columnNames == null || column > columnNames.size()) {
            return null;
        }
        return columnNames.get(column);
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        return StringUtils.EMPTY;
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        //TODO: Check if this is needed.
        return 9;
    }

    @Override
    public int getScale(int column) throws SQLException {
        return 9;
    }

    @Override
    public String getTableName(int column) throws SQLException {
        return StringUtils.EMPTY;
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        return StringUtils.EMPTY;
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        if (CollectionUtils.isEmpty(columnTypeIds) && !CollectionUtils.isEmpty(columnNames)) {
            return Types.VARCHAR;
        }
        return columnTypeIds.get(column - 1);
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        if (CollectionUtils.isEmpty(columnTypes) && !CollectionUtils.isEmpty(columnNames)) {
            return "VARCHAR";
        }
        return columnTypes.get(column);
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        return true;
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        return false;
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
