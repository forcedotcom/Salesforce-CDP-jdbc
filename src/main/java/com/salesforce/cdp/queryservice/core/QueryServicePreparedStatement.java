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

import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class QueryServicePreparedStatement extends QueryServiceAbstractStatement implements PreparedStatement {
    private String sql;

    private Map<Integer, Object> parameters = new HashMap<>();

    public QueryServicePreparedStatement(QueryServiceConnection queryServiceConnection, String sql,
                                         int resultSetType, int resultSetConcurrency) {
        super(queryServiceConnection, resultSetType, resultSetConcurrency);
        this.sql = sql;
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        String fullSql = createSqlQuery();
        resultSet = super.executeQuery(fullSql);
        return resultSet;
    }

    @Override
    public int executeUpdate() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        parameters.put(parameterIndex, null);
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        parameters.put(parameterIndex, x);
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        parameters.put(parameterIndex, x);
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        parameters.put(parameterIndex, x);
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        parameters.put(parameterIndex, x);
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        parameters.put(parameterIndex, x);
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        parameters.put(parameterIndex, x);
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        parameters.put(parameterIndex, "'" + x + "'");
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        parameters.put(parameterIndex, x);
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void clearParameters() throws SQLException {
        parameters.clear();
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public boolean execute() throws SQLException {
        String fullSql = createSqlQuery();
        resultSet = super.executeQuery(fullSql);
        return true;
    }

    @Override
    public void addBatch() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        //NOOP
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        //NOOP
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        //NOOP
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        //NOOP
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return null;
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        resultSet = super.executeQuery(sql);
        return resultSet;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {

    }

    @Override
    public int getMaxRows() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {

    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {

    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return 0;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        //TODO: Check if this needs to be set to execute with http client.
    }

    @Override
    public void cancel() throws SQLException {
        // TODO: Check if request can be cancelled.
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public void setCursorName(String name) throws SQLException {

    }

    @Override
    public boolean execute(String sql) throws SQLException {
        resultSet = super.executeQuery(sql);
        return true;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return resultSet;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        //NOOP
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return 0;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        //NOOP
    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetType() throws SQLException {
        return 0;
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public void clearBatch() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public int[] executeBatch() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return null;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return false;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {

    }

    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }

    @Override
    public void closeOnCompletion() throws SQLException {

    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    // Replaces ? in sql query with parameters
    @VisibleForTesting
    String createSqlQuery() throws SQLException {
        String sqlQuery = sql;
        int parameterIndex = 0;
        while(sqlQuery.contains("?")) {
            if (parameterIndex == parameters.size()) {
                log.error("Not enough parameters to replace placeholders in query.");
                throw new SQLException("Not enough parameters");
            }
            Object parameter = parameters.get(parameterIndex++);
            sqlQuery = sqlQuery.replaceFirst("\\?", parameter == null ? "null" : parameter.toString());
        }
        return sqlQuery;
    }
}
