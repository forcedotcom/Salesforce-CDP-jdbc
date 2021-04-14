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

import com.salesforce.cdp.queryservice.util.Constants;
import com.salesforce.cdp.queryservice.util.QueryExecutor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public class QueryServiceConnection implements Connection {

    private AtomicBoolean closed = new AtomicBoolean(false);
    private Properties properties;
    private String serviceRootUrl;
    private QueryExecutor queryExecutor;

    public QueryServiceConnection(String url, Properties properties) {
        this.properties = properties;
        this.serviceRootUrl = url.substring(Constants.DATASOURCE_TYPE.length());
        this.properties.put(Constants.LOGIN_URL, serviceRootUrl);
        this.queryExecutor = new QueryExecutor(properties);
    }

    @Override
    public Statement createStatement() throws SQLException {
        return createStatement(ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return new QueryServicePreparedStatement(this, sql,
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        throw new SQLException("Callable Statements are not supported in query service");
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        throw new SQLException("Only ANSI SQL queries are supported");
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        //NOOP as there is no updates happening from query service.
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return false;
    }

    @Override
    public void commit() throws SQLException {
        //NOOP as there is no updates happening from query service.
    }

    @Override
    public void rollback() throws SQLException {
        //NOOP as there is no updates happening from query service.
    }

    @Override
    public void close() throws SQLException {
        closed.set(true);
        byte [] passwordArray = (byte []) properties.get(Constants.PD);
        Arrays.fill(passwordArray, (byte) 0);
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed.get();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return new QueryServiceMetadata(this, serviceRootUrl, properties);
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        //NOOP as the service is RO.
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return true;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        //NOOP
    }

    @Override
    public String getCatalog() throws SQLException {
        return StringUtils.EMPTY;
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        //NOOP
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return Connection.TRANSACTION_NONE;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        //NOOP
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return new QueryServiceStatement(this, resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return new QueryServicePreparedStatement(this, sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new SQLFeatureNotSupportedException("Calling stored procedures are not supported");
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return null;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        //NOOP
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        //NOOP
    }

    @Override
    public int getHoldability() throws SQLException {
        return 0;
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return null;
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return null;
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        //NOOP
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        //NOOP
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new SQLFeatureNotSupportedException("Setting holdability is not supported");
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new SQLFeatureNotSupportedException("Setting holdability is not supported");
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new SQLFeatureNotSupportedException("Calling stored procedures are not supported");
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        throw new SQLFeatureNotSupportedException("Autogenerated keys not supported");
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        throw new SQLFeatureNotSupportedException("column Indexes not supported");
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        throw new SQLFeatureNotSupportedException("column Name not supported");
    }

    @Override
    public Clob createClob() throws SQLException {
        return null;
    }

    @Override
    public Blob createBlob() throws SQLException {
        return null;
    }

    @Override
    public NClob createNClob() throws SQLException {
        return null;
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return null;
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return BooleanUtils.isFalse(isClosed());
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        properties.put(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        //NOOP
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return properties.getProperty(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return properties;
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return null;
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return null;
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        //NOOP
    }

    @Override
    public String getSchema() throws SQLException {
        return StringUtils.EMPTY;
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        //NOOP
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        // TODO: Check if any timeout is needed for client if we can incorporate that to http client.
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public QueryExecutor getQueryExecutor() {
        return queryExecutor;
    }
}
