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
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.FieldVector;
import org.apache.arrow.vector.IntVector;
import org.apache.arrow.vector.VarCharVector;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.ipc.ArrowStreamReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import org.apache.arrow.vector.types.Types;
import org.apache.arrow.vector.util.Text;

@Slf4j
public abstract class QueryServiceAbstractStatement {
    protected QueryServiceConnection connection;

    protected ResultSet resultSet;

    protected int resultSetType;

    protected int resultSetConcurrency;

    protected int offset = 0;

    protected String sql;

    protected boolean paginationRequired;

    protected String nextBatchId;

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
            boolean isTableauQuery = isTableauQuery();
            Response response = queryExecutor.executeQuery(sql, isTableauQuery ? Optional.of(Constants.MAX_LIMIT) : Optional.empty(), Optional.of(offset), isTableauQuery ? Optional.of("1 ASC") : Optional.empty());
            if (!response.isSuccessful()) {
                log.error("Request query {} failed with response code {} and trace-Id {}", sql, response.code(), response.headers().get(Constants.TRACE_ID));
                HttpHelper.handleErrorResponse(response, Constants.MESSAGE);
            }
            QueryServiceResponse queryServiceResponse = HttpHelper.handleSuccessResponse(response, QueryServiceResponse.class, false);
            return createResultSetFromResponse(queryServiceResponse);
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
            return createResultSetFromResponse(queryServiceResponse);
        } catch (IOException e) {
            log.error("Exception while running the query", e);
            throw new SQLException(QUERY_EXCEPTION);
        }
    }

    private boolean isTableauQuery() throws SQLException {
        String userAgent = connection.getClientInfo(Constants.USER_AGENT);
        return Constants.TABLEAU_USER_AGENT_VALUE.equals(userAgent);
    }

    private ResultSet createResultSetFromResponse(QueryServiceResponse queryServiceResponse) throws SQLException {
        ArrowUtil arrowUtil = new ArrowUtil();
        paginationRequired = !queryServiceResponse.isDone();
        offset += queryServiceResponse.getRowCount();
        List<Map<String, Object>> data = null;
        if(this.connection.getEnableArrowStream() && queryServiceResponse.getArrowStream() != null) {
            data = arrowUtil.getResultSetDataFromArrowStream(queryServiceResponse, this.connection.isPrestoPaginatedRequest());
        }
        else {
            data = queryServiceResponse.getData();
        }

        if(this.connection.isPrestoPaginatedRequest() && queryServiceResponse.getNextBatchId() != null) {
            nextBatchId = queryServiceResponse.getNextBatchId();
        }
        QueryServiceResultSetMetaData resultSetMetaData = createColumnNames(queryServiceResponse);
        return new QueryServiceResultSet(data, resultSetMetaData, this);
    }

    private QueryServiceResultSetMetaData createColumnNames(QueryServiceResponse queryServiceResponse) throws SQLException {
        QueryServiceResultSetMetaData resultSetMetaData;
        List<String> columnNames = new ArrayList<>();
        List<String> columnTypes = new ArrayList<>();
        List<Integer> columnTypeIds = new ArrayList<>();
        if (queryServiceResponse.getMetadata() == null && queryServiceResponse.getRowCount() > 0) {
            Map<String, Object> row = queryServiceResponse.getData().get(0);
            columnNames = new ArrayList<>(row.keySet());
            columnTypes = Collections.EMPTY_LIST;
            columnTypeIds = Collections.EMPTY_LIST;
        } else if (queryServiceResponse.getMetadata() != null) {
            log.info("Metadata is {}", queryServiceResponse.getMetadata());
            Map<String, Type> metadata = queryServiceResponse.getMetadata();
            for (String columnName : metadata.keySet()) {
                columnNames.add(columnName);
                columnTypes.add(metadata.get(columnName).getType());
                columnTypeIds.add(metadata.get(columnName).getTypeCode());
            }
        } else {
            columnNames = Collections.EMPTY_LIST;
            columnTypes = Collections.EMPTY_LIST;
            columnTypeIds = Collections.EMPTY_LIST;
        }
        resultSetMetaData = new QueryServiceResultSetMetaData(columnNames, columnTypes, columnTypeIds);
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
