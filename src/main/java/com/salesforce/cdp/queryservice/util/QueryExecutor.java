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

package com.salesforce.cdp.queryservice.util;

import com.google.gson.Gson;
import com.salesforce.cdp.queryservice.auth.token.CoreToken;
import com.salesforce.cdp.queryservice.auth.TokenManager;
import com.salesforce.cdp.queryservice.auth.TokenUtils;
import com.salesforce.cdp.queryservice.core.QueryServiceConnection;
import com.salesforce.cdp.queryservice.interceptors.RetryInterceptor;
import com.salesforce.cdp.queryservice.model.AnsiQueryRequest;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@Slf4j
public class QueryExecutor extends QueryTokenExecutor {

    private static final OkHttpClient DEFAULT_QUERY_CLIENT;
    static {
        // By default, add retry interceptors only for query service related calls
        // todo: delay adding retry interceptor so that user configured value can be used
        DEFAULT_QUERY_CLIENT = DEFAULT_CLIENT.newBuilder()
                .addInterceptor(new RetryInterceptor(DEFAULT_MAX_RETRY))
                .build();
    }

    private final OkHttpClient queryClient;

    public QueryExecutor(QueryServiceConnection connection) {
        this(connection, null, null);
    }

    public QueryExecutor(QueryServiceConnection connection, OkHttpClient tokenClient, OkHttpClient client) {
        super(connection, tokenClient);
        client = client == null ? DEFAULT_QUERY_CLIENT : client;
        this.queryClient = updateClientWithSocketFactory(client, connection.isSocksProxyDisabled());
    }

    QueryExecutor(QueryServiceConnection connection, OkHttpClient tokenClient, OkHttpClient client, TokenManager tokenManager) {
        super(connection, tokenClient, tokenManager);
        client = client == null ? DEFAULT_QUERY_CLIENT : client;
        this.queryClient = updateClientWithSocketFactory(client, connection.isSocksProxyDisabled());
    }

    public Response executeQuery(String sql, boolean isV2Query, Optional<Integer> limit, Optional<Integer> offset, Optional<String> orderby) throws IOException, SQLException {
        // fixme: preferably, avoid using optional as parameter type, instead specify nullable value
        log.info("Preparing to execute query {}", sql);
        AnsiQueryRequest ansiQueryRequest = AnsiQueryRequest.builder().sql(sql).build();
        RequestBody body = RequestBody.create(new Gson().toJson(ansiQueryRequest), MediaType.parse(Constants.JSON_CONTENT));
        Map<String, String> tokenWithTenantUrl = getTokenWithTenantUrl();
        StringBuilder url = new StringBuilder(Constants.PROTOCOL)
                .append(tokenWithTenantUrl.get(Constants.TENANT_URL))
                .append(isV2Query ? Constants.CDP_URL_V2 : Constants.CDP_URL)
                .append(Constants.ANSI_SQL_URL)
                .append(Constants.QUESTION_MARK);
        limit.ifPresent(integer -> url.append(Constants.LIMIT).append(integer).append(Constants.AND));
        offset.ifPresent(integer -> url.append(Constants.OFFSET).append(integer).append(Constants.AND));
        orderby.ifPresent(s -> url.append(Constants.ORDERBY).append(s));
        Request request = HttpHelper.buildRequest(Constants.POST, url.toString(), body, createHeaders(tokenWithTenantUrl, this.connection.getEnableArrowStream()));
        return getResponse(request);
    }

    public Response executeNextBatchQuery(String nextBatchId) throws IOException, SQLException {
        log.info("Preparing to execute query for nextBatch {}", nextBatchId);
        Map<String, String> tokenWithTenantUrl = getTokenWithTenantUrl();
        String url = Constants.PROTOCOL + tokenWithTenantUrl.get(Constants.TENANT_URL)
                + Constants.CDP_URL_V2
                + Constants.ANSI_SQL_URL
                + Constants.SLASH
                + nextBatchId;

        Request request = HttpHelper.buildRequest(Constants.GET, url, null, createHeaders(tokenWithTenantUrl, this.connection.getEnableArrowStream()));
        return getResponse(request);
    }

    public Response getMetadata() throws IOException, SQLException {
        log.info("Getting metadata from CDP query service");
        Map<String, String> tokenWithTenantUrl = getTokenWithTenantUrl();
        String url = Constants.PROTOCOL + tokenWithTenantUrl.get(Constants.TENANT_URL)
                + Constants.CDP_URL
                + Constants.METADATA_URL;
        Request request = HttpHelper.buildRequest(Constants.GET, url, null, createHeaders(tokenWithTenantUrl, false));
        return getResponse(request);
    }

    public Response getQueryConfig() throws IOException, SQLException {
        log.info("Getting query config from CDP Query Service");
        Map<String, String> tokenWithTenantUrl = getTokenWithTenantUrl();
        String url = Constants.PROTOCOL + tokenWithTenantUrl.get(Constants.TENANT_URL)
                + Constants.CDP_URL
                + Constants.QUERY_CONFIG_URL;

        Map<String, String> headers = createHeaders(tokenWithTenantUrl, false);
        headers.put(Constants.ENABLE_STREAM_FLOW, String.valueOf(this.connection.isEnableStreamFlow()));

        Request request = HttpHelper.buildRequest(Constants.GET, url, null, headers);
        return getResponse(request);
    }

    private Map<String, String> createHeaders(Map<String, String> tokenWithTenantUrl, boolean enableArrowStream) throws SQLException {
        Properties properties = connection.getClientInfo();
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.AUTHORIZATION, tokenWithTenantUrl.get(Constants.ACCESS_TOKEN));
        headers.put(Constants.CONTENT_TYPE, Constants.JSON_CONTENT);
        if(enableArrowStream) {
            headers.put(Constants.ENABLE_ARROW_STREAM, Constants.TRUE_STR);
        }
        if (properties.containsKey(Constants.USER_AGENT)) {
            headers.put(Constants.USER_AGENT, properties.get(Constants.USER_AGENT).toString());
        }
        return headers;
    }

    protected Response getResponse(Request request) throws IOException {
        long startTime = System.currentTimeMillis();
        // use queryClient to fetch metadata or to execute the query
        Response response = queryClient.newCall(request).execute();
        long endTime = System.currentTimeMillis();
        log.info("Total time taken to get response for url {} is {} ms and traceid {}", request.url(), endTime - startTime, response.headers(Constants.TRACE_ID));
        return response;
    }

    public Response getDataspaces()  {
        try{

            Properties connectionProperties = connection.getClientInfo();
            String url = connectionProperties.getProperty(Constants.LOGIN_URL)+Constants.DATASPACE_URL;
            CoreToken coreToken = getCoreToken();
            Map<String, String> tokenWithUrl = TokenUtils.getTokenWithUrl(coreToken);
            Map<String, String> headers = new HashMap<>();
            headers.put(Constants.AUTHORIZATION, tokenWithUrl.get(Constants.ACCESS_TOKEN));
            headers.put(Constants.CONTENT_TYPE, Constants.JSON_CONTENT);

            Request request = HttpHelper.buildRequest(Constants.GET, url, null,headers );
            return getResponse(request);
        }
        catch(Exception e){
            log.error("Error while fetching dataspace",e);
        }
        return null;
    }


}
