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

import com.salesforce.cdp.queryservice.model.MetadataResponse;
import com.salesforce.cdp.queryservice.model.TableMetadata;
import com.salesforce.cdp.queryservice.util.Constants;
import com.salesforce.cdp.queryservice.util.HttpHelper;
import static com.salesforce.cdp.queryservice.util.Messages.METADATA_EXCEPTION;
import com.salesforce.cdp.queryservice.util.QueryExecutor;
import com.salesforce.cdp.queryservice.util.Utils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

import static com.salesforce.cdp.queryservice.core.QueryServiceDbMetadata.*;

@Slf4j
public class QueryServiceMetadata implements DatabaseMetaData {
    private String url;
    private Properties properties;
    private QueryServiceConnection queryServiceConnection;
    private QueryExecutor queryExecutor;

    public QueryServiceMetadata(QueryServiceConnection queryServiceConnection, String url, Properties properties) {
        this.url = url;
        this.properties = properties;
        this.queryServiceConnection = queryServiceConnection;
        this.queryExecutor = createQueryExecutor();
    }

    @Override
    public boolean allProceduresAreCallable() throws SQLException {
        throw new SQLFeatureNotSupportedException("Procedures are not supported");
    }

    @Override
    public boolean allTablesAreSelectable() throws SQLException {
        return true;
    }

    @Override
    public String getURL() throws SQLException {
        return Constants.DATASOURCE_TYPE + url;
    }

    @Override
    public String getUserName() throws SQLException {
        return properties.get(Constants.USER_NAME).toString();
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return true;
    }

    @Override
    public boolean nullsAreSortedHigh() throws SQLException {
        return false;
    }

    @Override
    public boolean nullsAreSortedLow() throws SQLException {
        return false;
    }

    @Override
    public boolean nullsAreSortedAtStart() throws SQLException {
        return false;
    }

    @Override
    public boolean nullsAreSortedAtEnd() throws SQLException {
        return false;
    }

    @Override
    public String getDatabaseProductName() throws SQLException {
        return Constants.QUERY_SERVICE;
    }

    @Override
    public String getDatabaseProductVersion() throws SQLException {
        return Constants.QUERY_SERVICE_VERSION;
    }

    @Override
    public String getDriverName() throws SQLException {
        return Constants.DRIVER_NAME;
    }

    @Override
    public String getDriverVersion() throws SQLException {
        return Constants.DRIVER_VERSION;
    }

    @Override
    public int getDriverMajorVersion() {
        return 1;
    }

    @Override
    public int getDriverMinorVersion() {
        return 0;
    }

    @Override
    public boolean usesLocalFiles() throws SQLException {
        return false;
    }

    @Override
    public boolean usesLocalFilePerTable() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesUpperCaseIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesLowerCaseIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesMixedCaseIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    @Override
    public String getIdentifierQuoteString() throws SQLException {
        return "\"";
    }

    @Override
    public String getSQLKeywords() throws SQLException {
        return null;
    }

    @Override
    public String getNumericFunctions() throws SQLException {
        return null;
    }

    @Override
    public String getStringFunctions() throws SQLException {
        return null;
    }

    @Override
    public String getSystemFunctions() throws SQLException {
        return null;
    }

    @Override
    public String getTimeDateFunctions() throws SQLException {
        return null;
    }

    @Override
    public String getSearchStringEscape() throws SQLException {
        return null;
    }

    @Override
    public String getExtraNameCharacters() throws SQLException {
        return null;
    }

    @Override
    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        throw new SQLFeatureNotSupportedException("Updating table is not supported");
    }

    @Override
    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        throw new SQLFeatureNotSupportedException("Updating table is not supported");
    }

    @Override
    public boolean supportsColumnAliasing() throws SQLException {
        return true;
    }

    @Override
    public boolean nullPlusNonNullIsNull() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsConvert() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsConvert(int fromType, int toType) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public boolean supportsTableCorrelationNames() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public boolean supportsExpressionsInOrderBy() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsOrderByUnrelated() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsGroupBy() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsGroupByUnrelated() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsGroupByBeyondSelect() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsLikeEscapeClause() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsMultipleResultSets() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsMultipleTransactions() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsNonNullableColumns() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsMinimumSQLGrammar() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsCoreSQLGrammar() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsExtendedSQLGrammar() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsANSI92FullSQL() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public boolean supportsOuterJoins() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsFullOuterJoins() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsLimitedOuterJoins() throws SQLException {
        return true;
    }

    @Override
    public String getSchemaTerm() throws SQLException {
        return null;
    }

    @Override
    public String getProcedureTerm() throws SQLException {
        return "";
    }

    @Override
    public String getCatalogTerm() throws SQLException {
        return null;
    }

    @Override
    public boolean isCatalogAtStart() throws SQLException {
        return false;
    }

    @Override
    public String getCatalogSeparator() throws SQLException {
        return "";
    }

    @Override
    public boolean supportsSchemasInDataManipulation() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsPositionedDelete() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsPositionedUpdate() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSelectForUpdate() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsStoredProcedures() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsSubqueriesInComparisons() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsSubqueriesInExists() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsSubqueriesInIns() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsCorrelatedSubqueries() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsUnion() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsUnionAll() throws SQLException {
        return true;
    }

    @Override
    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        return false;
    }

    @Override
    public int getMaxBinaryLiteralLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxCharLiteralLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxColumnNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxColumnsInGroupBy() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxColumnsInIndex() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxColumnsInOrderBy() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxColumnsInSelect() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxColumnsInTable() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxConnections() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxCursorNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxIndexLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxSchemaNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxProcedureNameLength() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public int getMaxCatalogNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxRowSize() throws SQLException {
        return 0;
    }

    @Override
    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        return false;
    }

    @Override
    public int getMaxStatementLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxStatements() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxTableNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxTablesInSelect() throws SQLException {
        return 0;
    }

    @Override
    public int getMaxUserNameLength() throws SQLException {
        return 0;
    }

    @Override
    public int getDefaultTransactionIsolation() throws SQLException {
        return 0;
    }

    @Override
    public boolean supportsTransactions() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
        return false;
    }

    @Override
    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        return false;
    }

    @Override
    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        return false;
    }

    @Override
    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        return false;
    }

    @Override
    public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
        return new QueryServiceResultSet(Collections.EMPTY_LIST,
                new QueryServiceResultSetMetaData(Collections.EMPTY_LIST, Collections.EMPTY_LIST, Collections.EMPTY_LIST));
    }

    @Override
    public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
        return new QueryServiceResultSet(Collections.EMPTY_LIST,
                new QueryServiceResultSetMetaData(Collections.EMPTY_LIST, Collections.EMPTY_LIST, Collections.EMPTY_LIST));
    }

    @Override
    public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
        try {
            Response response = queryExecutor.getMetadata();
            if (!response.isSuccessful()) {
                log.error("Metadata request failed with response code {} and trace-Id {}", response.code(), response.headers().get(Constants.TRACE_ID));
                HttpHelper.handleErrorResponse(response, Constants.MESSAGE);
            }
            MetadataResponse metadataResponse = HttpHelper.handleSuccessResponse(response, MetadataResponse.class, true);
            return createTableResultSet(metadataResponse, tableNamePattern);
        } catch (IOException e) {
            log.info("Exception while getting metadata from query service", e);
            throw new SQLException(METADATA_EXCEPTION);
        } finally {
            queryServiceConnection.close();
        }
    }

    @Override
    public ResultSet getSchemas() throws SQLException {
        return new QueryServiceResultSet(Collections.EMPTY_LIST,
                new QueryServiceResultSetMetaData(GET_SCHEMAS));
    }

    @Override
    public ResultSet getCatalogs() throws SQLException {
        return new QueryServiceResultSet(Collections.EMPTY_LIST,
                new QueryServiceResultSetMetaData(GET_CATALOGS));
    }

    @Override
    public ResultSet getTableTypes() throws SQLException {
        return new QueryServiceResultSet(Collections.EMPTY_LIST,
                new QueryServiceResultSetMetaData(GET_TABLE_TYPES));
    }

    @Override
    public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        try {
            Response response = queryExecutor.getMetadata();
            if (!response.isSuccessful()) {
                log.error("Metadata request failed with response code {} and trace-Id {}", response.code(), response.headers().get(Constants.TRACE_ID));
                HttpHelper.handleErrorResponse(response, Constants.MESSAGE);
            }
            MetadataResponse metadataResponse = HttpHelper.handleSuccessResponse(response, MetadataResponse.class, true);
            return createColumnResultSet(metadataResponse, tableNamePattern);
        } catch (IOException e) {
            log.error("Exception while getting metadata from query service", e);
            throw new SQLException(METADATA_EXCEPTION);
        } finally {
            queryServiceConnection.close();
        }
    }

    @Override
    public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
        return new QueryServiceResultSet(Collections.EMPTY_LIST,
                new QueryServiceResultSetMetaData(EMPTY));
    }

    @Override
    public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        return new QueryServiceResultSet(Collections.EMPTY_LIST,
                new QueryServiceResultSetMetaData(GET_TABLE_PRIVILEGES));
    }

    @Override
    public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
        return new QueryServiceResultSet(Collections.EMPTY_LIST,
                new QueryServiceResultSetMetaData(EMPTY));
    }

    @Override
    public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
        return new QueryServiceResultSet(Collections.EMPTY_LIST,
                new QueryServiceResultSetMetaData(EMPTY));
    }

    @Override
    public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
        return new QueryServiceResultSet(Collections.EMPTY_LIST,
                new QueryServiceResultSetMetaData(GET_PRIMARY_KEYS));
    }

    @Override
    public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
        return new QueryServiceResultSet(Collections.EMPTY_LIST,
                new QueryServiceResultSetMetaData(EMPTY));
    }

    @Override
    public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
        return new QueryServiceResultSet(Collections.EMPTY_LIST,
                new QueryServiceResultSetMetaData(EMPTY));
    }

    @Override
    public ResultSet getCrossReference(String parentCatalog, String parentSchema, String parentTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
        return new QueryServiceResultSet(Collections.EMPTY_LIST,
                new QueryServiceResultSetMetaData(EMPTY));
    }

    @Override
    public ResultSet getTypeInfo() throws SQLException {
        return new QueryServiceResultSet(Collections.EMPTY_LIST,
                new QueryServiceResultSetMetaData(EMPTY));
    }

    @Override
    public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
        return new QueryServiceResultSet(Collections.EMPTY_LIST,
                new QueryServiceResultSetMetaData(EMPTY));
    }

    @Override
    public boolean supportsResultSetType(int type) throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY == type;
    }

    @Override
    public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
       return ResultSet.TYPE_FORWARD_ONLY == type && ResultSet.CONCUR_READ_ONLY == concurrency;
    }

    @Override
    public boolean ownUpdatesAreVisible(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean ownDeletesAreVisible(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean ownInsertsAreVisible(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean othersUpdatesAreVisible(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean othersDeletesAreVisible(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean othersInsertsAreVisible(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean updatesAreDetected(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean deletesAreDetected(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean insertsAreDetected(int type) throws SQLException {
        return false;
    }

    @Override
    public boolean supportsBatchUpdates() throws SQLException {
        return false;
    }

    @Override
    public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
        return null;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return queryServiceConnection;
    }

    @Override
    public boolean supportsSavepoints() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsNamedParameters() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsMultipleOpenResults() throws SQLException {
        return false;
    }

    @Override
    public boolean supportsGetGeneratedKeys() throws SQLException {
        return false;
    }

    @Override
    public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
        return null;
    }

    @Override
    public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
        return null;
    }

    @Override
    public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException {
        return null;
    }

    @Override
    public boolean supportsResultSetHoldability(int holdability) throws SQLException {
        return false;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return 0;
    }

    @Override
    public int getDatabaseMajorVersion() throws SQLException {
        return 1;
    }

    @Override
    public int getDatabaseMinorVersion() throws SQLException {
        return 0;
    }

    @Override
    public int getJDBCMajorVersion() throws SQLException {
        return 1;
    }

    @Override
    public int getJDBCMinorVersion() throws SQLException {
        return 0;
    }

    @Override
    public int getSQLStateType() throws SQLException {
        return 0;
    }

    @Override
    public boolean locatorsUpdateCopy() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    @Override
    public boolean supportsStatementPooling() throws SQLException {
        return false;
    }

    @Override
    public RowIdLifetime getRowIdLifetime() throws SQLException {
        return null;
    }

    @Override
    public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
        return null;
    }

    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
        return false;
    }

    @Override
    public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
        return false;
    }

    @Override
    public ResultSet getClientInfoProperties() throws SQLException {
        return null;
    }

    @Override
    public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
        return null;
    }

    @Override
    public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
        return null;
    }

    @Override
    public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
        return null;
    }

    @Override
    public boolean generatedKeyAlwaysReturned() throws SQLException {
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

    private ResultSet createTableResultSet(MetadataResponse metadataResponse, String tableNamePattern) {
        ResultSet resultSet = null;
        boolean needAllTables = tableNamePattern == null || tableNamePattern.isEmpty();
        QueryServiceDbMetadata dbMetadata = GET_TABLES;
        if (CollectionUtils.isEmpty(metadataResponse.getMetadata())) {
            log.info("No metadata for this org");
            resultSet = new QueryServiceResultSet(Collections.EMPTY_LIST, new QueryServiceResultSetMetaData(dbMetadata));
        } else if (needAllTables) {
            List<Map<String, Object>> data = new ArrayList<>();
            for (TableMetadata metadata : metadataResponse.getMetadata()) {
                data.add(createRow(metadata));
                resultSet = new QueryServiceResultSet(data, new QueryServiceResultSetMetaData(dbMetadata));
            }
        } else {
            String tableNameRegex = Utils.convertPatternToRegEx(tableNamePattern);
            List<Map<String, Object>> data = new ArrayList<>();
            for (TableMetadata metadata : metadataResponse.getMetadata()) {
                if (Pattern.matches(tableNameRegex, metadata.getName())) {
                    data.add(createRow(metadata));
                }
            }
            resultSet = new QueryServiceResultSet(data, new QueryServiceResultSetMetaData(dbMetadata));
        }
        return resultSet;
    }

    private Map<String, Object> createRow(TableMetadata metadata) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("TABLE_CAT", "QueryService");
        row.put("TABLE_SCHEM", null);
        row.put("TABLE_NAME", metadata.getName());
        row.put("TABLE_TYPE", "TABLE");
        row.put("REMARKS", StringUtils.EMPTY);
        row.put("TYPE_CAT", StringUtils.EMPTY);
        row.put("TYPE_SCHEM", StringUtils.EMPTY);
        row.put("TYPE_NAME", StringUtils.EMPTY);
        row.put("SELF_REFERENCING_COL_NAME", StringUtils.EMPTY);
        row.put("REF_GENERATION", StringUtils.EMPTY);
        return row;
    }

    private ResultSet createColumnResultSet(MetadataResponse metadataResponse, String tableNamePattern) {
        ResultSet resultSet;
        QueryServiceDbMetadata dbMetadata = GET_COLUMNS;
        if (CollectionUtils.isEmpty(metadataResponse.getMetadata())) {
            log.info("No metadata for this org");
            resultSet = new QueryServiceResultSet(Collections.EMPTY_LIST, new QueryServiceResultSetMetaData(dbMetadata));
        } else {
            List<Map<String, Object>> data = new ArrayList<>();
            for (TableMetadata metadata : metadataResponse.getMetadata()) {
                if (!metadata.getName().equals(tableNamePattern)) {
                    continue;
                }
                log.info("Table with name {} is present in the metadata. Fetching the columns.", tableNamePattern);
                List<Map<String, String>> columns = new ArrayList<>();
                if (!CollectionUtils.isEmpty(metadata.getFields())) {
                    columns.addAll(metadata.getFields());
                } else {
                    if(!CollectionUtils.isEmpty(metadata.getDimensions())) {
                        columns.addAll(metadata.getDimensions());
                    }
                    if (!CollectionUtils.isEmpty(metadata.getMeasures())) {
                        columns.addAll(metadata.getMeasures());
                    }
                }
                for (int i = 0; i < columns.size(); i++) {
                    HashMap<String, Object> columnMap = new LinkedHashMap<>();
                    columnMap.put("TABLE_CAT", "catalog");
                    columnMap.put("TABLE_SCHEM", null);
                    columnMap.put("TABLE_NAME", tableNamePattern);
                    columnMap.put("COLUMN_NAME", columns.get(i).get("name"));
                    columnMap.put("DATA_TYPE", JavaType.getTypeByName(columns.get(i).get("type")).getVendorTypeNumber());
                    columnMap.put("TYPE_NAME", columns.get(i).get("type"));
                    columnMap.put("COLUMN_SIZE", 255);
                    columnMap.put("BUFFER_LENGTH", StringUtils.EMPTY);
                    columnMap.put("DECIMAL_DIGITS", 2);
                    columnMap.put("NUM_PREC_RADIX", 10);
                    columnMap.put("NULLABLE", 1);
                    columnMap.put("REMARKS", StringUtils.SPACE);
                    columnMap.put("COLUMN_DEF", StringUtils.SPACE);
                    columnMap.put("SQL_DATA_TYPE", JavaType.valueOf(columns.get(i).get("type")));
                    columnMap.put("SQL_DATETIME_SUB", StringUtils.EMPTY);
                    columnMap.put("CHAR_OCTET_LENGTH", 2);
                    columnMap.put("ORDINAL_POSITION", 0);
                    columnMap.put("IS_NULLABLE", "YES");
                    columnMap.put("SCOPE_CATALOG", null);
                    columnMap.put("SCOPE_SCHEMA", null);
                    columnMap.put("SCOPE_TABLE", null);
                    columnMap.put("SOURCE_DATA_TYPE", null);
                    columnMap.put("IS_AUTOINCREMENT", StringUtils.SPACE);
                    columnMap.put("IS_GENERATEDCOLUMN", StringUtils.SPACE);
                    data.add(columnMap);
                }
            }
            resultSet = new QueryServiceResultSet(data, new QueryServiceResultSetMetaData(dbMetadata));
        }
        return resultSet;
    }

    protected QueryExecutor createQueryExecutor() {
        return new QueryExecutor(queryServiceConnection);
    }
}
