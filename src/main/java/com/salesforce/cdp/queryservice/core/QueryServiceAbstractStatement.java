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

import com.salesforce.cdp.queryservice.model.QueryServiceResponse;
import com.salesforce.cdp.queryservice.model.Type;
import com.salesforce.cdp.queryservice.util.ArrowUtil;
import com.salesforce.cdp.queryservice.util.Constants;
import static com.salesforce.cdp.queryservice.util.Messages.QUERY_EXCEPTION;
import com.salesforce.cdp.queryservice.util.QueryExecutor;
import com.salesforce.cdp.queryservice.util.HttpHelper;
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

    public QueryServiceAbstractStatement(QueryServiceConnection queryServiceConnection,
                                         int resultSetType,
                                         int resultSetConcurrency) {
        this.connection = queryServiceConnection;
        this.resultSetType = resultSetType;
        this.resultSetConcurrency = resultSetConcurrency;
        this.queryExecutor = createQueryExecutor();
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        try {
            this.sql = sql;
            boolean isCursorBasedPaginationReq = this.connection.isCursorBasedPaginationReq();

            boolean requireManagedPagination = isTableauQuery() && !isCursorBasedPaginationReq;
            Optional<Integer> limit = requireManagedPagination ? Optional.of(Constants.MAX_LIMIT) : Optional.empty();
            Optional<String> orderby = requireManagedPagination ? Optional.of("1 ASC") : Optional.empty();



            Response response = queryExecutor.executeQuery(sql, isCursorBasedPaginationReq, limit, Optional.of(offset), orderby);
            if (!response.isSuccessful()) {
                log.error("Request query {} failed with response code {} and trace-Id {}", sql, response.code(), response.headers().get(Constants.TRACE_ID));
                HttpHelper.handleErrorResponse(response, Constants.MESSAGE);
            }
            QueryServiceResponse queryServiceResponse = HttpHelper.handleSuccessResponse(response, QueryServiceResponse.class, false);
            return createResultSetFromResponse(queryServiceResponse, isCursorBasedPaginationReq);
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


}
