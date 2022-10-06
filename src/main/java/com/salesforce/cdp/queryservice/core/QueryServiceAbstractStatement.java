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

import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.salesforce.a360.queryservice.grpc.v1.AnsiSqlExtractQueryResponse;
import com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryStreamResponse;
import com.salesforce.cdp.queryservice.model.QueryServiceResponse;
import com.salesforce.cdp.queryservice.model.Type;
import com.salesforce.cdp.queryservice.util.ArrowUtil;
import com.salesforce.cdp.queryservice.util.Constants;

import static com.salesforce.cdp.queryservice.util.Messages.METADATA_EXCEPTION;
import static com.salesforce.cdp.queryservice.util.Messages.QUERY_EXCEPTION;
import com.salesforce.cdp.queryservice.util.QueryExecutor;
import com.salesforce.cdp.queryservice.util.HttpHelper;
import com.salesforce.cdp.queryservice.util.QueryGrpcExecutor;
import com.salesforce.cdp.queryservice.util.QueryRainbowExecutor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
public abstract class QueryServiceAbstractStatement {
    protected QueryServiceConnection connection;

    protected ResultSet resultSet;

    protected int resultSetType;

    protected int resultSetConcurrency;

    protected int offset = 0;

    protected String sql;

    protected boolean paginationRequired;

    private QueryExecutor queryExecutor;

    private QueryGrpcExecutor queryGrpcExecutor;

    private QueryRainbowExecutor queryRainbowExecutor;

    private static final String KEY_TYPE = "type";
    private static final String KEY_TYPE_CODE = "typeCode";
    private static final String KEY_PLACE_IN_ORDER = "placeInOrder";

    public QueryServiceAbstractStatement(QueryServiceConnection queryServiceConnection,
                                         int resultSetType,
                                         int resultSetConcurrency) {
        this.connection = queryServiceConnection;
        this.resultSetType = resultSetType;
        this.resultSetConcurrency = resultSetConcurrency;
        this.queryExecutor = createQueryExecutor();
        this.queryGrpcExecutor = createQueryGrpcExecutor();
        this.queryRainbowExecutor = createQueryRainbowExecutor();
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        try {
            this.sql = sql;
            boolean isEnableStreamFlow = this.connection.isEnableStreamFlow();

            boolean isCursorBasedPaginationReq = this.connection.isCursorBasedPaginationReq();

            boolean requireManagedPagination = isTableauQuery() && !isCursorBasedPaginationReq;
            Optional<Integer> limit = requireManagedPagination ? Optional.of(Constants.MAX_LIMIT) : Optional.empty();
            Optional<String> orderby = requireManagedPagination ? Optional.of("1 ASC") : Optional.empty();
            if(connection.isRainbowConnection()){
                    Iterator<AnsiSqlExtractQueryResponse> response = queryRainbowExecutor.executeQuery(sql);
                    return createResultSetFromRainbowResponse(response);
            }
            else if(isEnableStreamFlow) {
                Iterator<AnsiSqlQueryStreamResponse> response = queryGrpcExecutor.executeQueryWithRetry(sql);
                return createResultSetFromResponse(response);
            } else {
                Response response = queryExecutor.executeQuery(sql, isCursorBasedPaginationReq, limit, Optional.of(offset), orderby);

                if (!response.isSuccessful()) {
                    log.error("Request query {} failed with response code {} and trace-Id {}", sql, response.code(), response.headers().get(Constants.TRACE_ID));
                    HttpHelper.handleErrorResponse(response, Constants.MESSAGE);
                }
                QueryServiceResponse queryServiceResponse = HttpHelper.handleSuccessResponse(response, QueryServiceResponse.class, false);
                return createResultSetFromResponse(queryServiceResponse, isCursorBasedPaginationReq);
            }
        } catch (IOException e) {
            log.error("Exception while running the query", e);
            throw new SQLException(QUERY_EXCEPTION);
        }
    }

    public ResultSet executeNextBatchQuery(String nextBatchId) throws SQLException {
        try {
            Response response = queryExecutor.executeNextBatchQuery(nextBatchId);
            if (!response.isSuccessful()) {
                log.error("Request query {} failed with response code {} and trace-Id {}", sql, response.code(), response.headers().get(Constants.TRACE_ID));
                HttpHelper.handleErrorResponse(response, Constants.MESSAGE);
            }
            QueryServiceResponse queryServiceResponse = HttpHelper.handleSuccessResponse(response, QueryServiceResponse.class, false);
            return createResultSetFromResponse(queryServiceResponse, true);
        } catch (IOException e) {
            log.error("Exception while running the query", e);
            throw new SQLException(QUERY_EXCEPTION, e);
        }
    }

    private boolean isTableauQuery() throws SQLException {
        String userAgent = connection.getClientInfo(Constants.USER_AGENT);
        return Constants.TABLEAU_USER_AGENT_VALUE.equals(userAgent);
    }
    private ResultSet createResultSetFromRainbowResponse(Iterator<AnsiSqlExtractQueryResponse> response) throws SQLException {
        try{
            return new RainbowQueryResultSet(response,this);
        }
        catch (Exception e){
            throw new SQLException(QUERY_EXCEPTION);
        }
    }
    private ResultSet createResultSetFromResponse(QueryServiceResponse queryServiceResponse, boolean isCursorBasedPaginationReq) throws SQLException {
        ArrowUtil arrowUtil = new ArrowUtil();
        paginationRequired = !queryServiceResponse.isDone();
        offset += queryServiceResponse.getRowCount();
        List<Object> data;
        if(this.connection.getEnableArrowStream() && queryServiceResponse.getArrowStream() != null) {
            data = arrowUtil.getResultSetDataFromArrowStream(queryServiceResponse, isCursorBasedPaginationReq);
        }
        else {
            data = queryServiceResponse.getData();
        }

        QueryServiceResultSetMetaData resultSetMetaData = createColumnNames(queryServiceResponse);

        if(isCursorBasedPaginationReq) {
            return new QueryServiceResultSetV2(data, resultSetMetaData, this, queryServiceResponse.getNextBatchId());
        }
        return new QueryServiceResultSet(data, resultSetMetaData, this);
    }

    private ResultSet createResultSetFromResponse(Iterator<AnsiSqlQueryStreamResponse> queryServiceResponse) throws SQLException {
        try {
            if(queryServiceResponse.hasNext()) {
                // first batch is metadata
                QueryServiceResultSetMetaData resultSetMetaData = createColumnNames(queryServiceResponse.next().getMetadata().getMetadata());
                return new QueryServiceHyperResultSet(queryServiceResponse, resultSetMetaData, this);
            }

            // If no metadata and no exception, throw exception
            throw new SQLException(METADATA_EXCEPTION);
        } catch (Exception e) {
            log.error("Exception in executing query ", e);
            throw new SQLException(e.getMessage());
        }
    }

    private QueryServiceResultSetMetaData createColumnNames(QueryServiceResponse queryServiceResponse) throws SQLException {
        QueryServiceResultSetMetaData resultSetMetaData;
        List<String> columnNames = new ArrayList<>();
        List<String> columnTypes = new ArrayList<>();
        List<Integer> columnTypeIds = new ArrayList<>();
        Map<String, Integer> columnNameToPosition = new HashMap<>();

        if (queryServiceResponse.getMetadata() == null && queryServiceResponse.getRowCount() > 0) {
            throw new SQLException(QUERY_EXCEPTION);
        } else if (queryServiceResponse.getMetadata() != null) {
            log.debug("Metadata is {}", queryServiceResponse.getMetadata());
            Map<String, Type> metadata = queryServiceResponse.getMetadata();
            for (String columnName : metadata.keySet()) {
                final Type type = metadata.get(columnName);
                columnNames.add(columnName);
                columnTypes.add(type.getType());
                columnTypeIds.add(type.getTypeCode());
                columnNameToPosition.put(columnName, type.getPlaceInOrder());
            }
        }

        resultSetMetaData = new QueryServiceResultSetMetaData(columnNames, columnTypes, columnTypeIds, columnNameToPosition);
        log.trace("Received column names are {}", columnNames);
        return resultSetMetaData;
    }

    private QueryServiceResultSetMetaData createColumnNames(Struct metadata) throws SQLException {
        QueryServiceResultSetMetaData resultSetMetaData;
        List<String> columnNames = new ArrayList<>();
        List<String> columnTypes = new ArrayList<>();
        List<Integer> columnTypeIds = new ArrayList<>();
        Map<String, Integer> columnNameToPosition = new HashMap<>();

        if (metadata == null) {
            throw new SQLException(QUERY_EXCEPTION);
        } else {
            log.debug("Metadata is {}", metadata);
            try {
                Map<String, Value> metadataMap = metadata.getFieldsMap();
                for (String columnName : metadataMap.keySet()) {
                    columnNames.add(columnName);
                    final Map<String, Value> metadataValue = metadataMap.get(columnName).getStructValue().getFieldsMap();
                    columnTypes.add(metadataValue.get(KEY_TYPE).getStringValue());
                    columnTypeIds.add((int)metadataValue.get(KEY_TYPE_CODE).getNumberValue());
                    columnNameToPosition.put(columnName, (int)metadataValue.get(KEY_PLACE_IN_ORDER).getNumberValue());
                }
            } catch (Exception e) {
                log.debug("Exception while parsing metadata struct");
                throw new SQLException(METADATA_EXCEPTION);
            }
        }

        resultSetMetaData = new QueryServiceResultSetMetaData(columnNames, columnTypes, columnTypeIds, columnNameToPosition);
        log.trace("Received column names are {}", columnNames);
        return resultSetMetaData;
    }

    public boolean isPaginationRequired() {
        return paginationRequired;
    }

    public ResultSet getNextPage() throws SQLException {
        return this.executeQuery(sql);
    }

    public ResultSet getNextPageFromBatchId(String nextBatchId) throws SQLException {
        return this.executeNextBatchQuery(nextBatchId);
    }

    protected QueryExecutor createQueryExecutor() {
        return new QueryExecutor(connection);
    }

    protected QueryGrpcExecutor createQueryGrpcExecutor() {
        return new QueryGrpcExecutor(connection);
    }

    protected QueryRainbowExecutor createQueryRainbowExecutor(){
        return new QueryRainbowExecutor(connection);
    }
}
