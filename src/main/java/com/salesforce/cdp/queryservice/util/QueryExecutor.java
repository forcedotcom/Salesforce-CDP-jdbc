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
import com.salesforce.cdp.queryservice.core.QueryServiceConnection;
import com.salesforce.cdp.queryservice.interceptors.MetadataCacheInterceptor;
import com.salesforce.cdp.queryservice.interceptors.RetryInterceptor;
import com.salesforce.cdp.queryservice.model.AnsiQueryRequest;
import com.salesforce.cdp.queryservice.model.Token;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Slf4j
public class QueryExecutor {

    private QueryServiceConnection connection;

    private OkHttpClient client;

    public QueryExecutor(QueryServiceConnection connection) {
        this.connection = connection;
        client = createClient();
    }

    public Response executeQuery(String sql, boolean isV2Query, Optional<Integer> limit, Optional<Integer> offset, Optional<String> orderby) throws IOException, SQLException {
        log.info("Preparing to execute query {}", sql);
        AnsiQueryRequest ansiQueryRequest = AnsiQueryRequest.builder().sql(sql).build();
        RequestBody body = RequestBody.create(MediaType.parse(Constants.JSON_CONTENT), new Gson().toJson(ansiQueryRequest));
        Map<String, String> tokenWithTenantUrl = getTokenWithTenantUrl();
        StringBuilder url = new StringBuilder(Constants.PROTOCOL + tokenWithTenantUrl.get(Constants.TENANT_URL)
                + (isV2Query ? Constants.CDP_URL_V2: Constants.CDP_URL)
                + Constants.ANSI_SQL_URL
                + Constants.QUESTION_MARK);

        if (limit.isPresent()) {
            url.append(Constants.LIMIT + limit.get() + Constants.AND);
        }
        if (offset.isPresent()) {
            url.append(Constants.OFFSET + offset.get() + Constants.AND);
        }
        if (orderby.isPresent()) {
            url.append(Constants.ORDERBY + orderby.get());
        }
        Request request = HttpHelper.buildRequest(Constants.POST, url.toString(), body, createHeaders(tokenWithTenantUrl, this.connection.getEnableArrowStream()));
        return getResponse(request);
    }

    public Response executeNextBatchQuery(String nextBatchId) throws IOException, SQLException {
        log.info("Preparing to execute query for nextBatch {}", nextBatchId);
        Map<String, String> tokenWithTenantUrl = getTokenWithTenantUrl();
        StringBuilder url = new StringBuilder(Constants.PROTOCOL + tokenWithTenantUrl.get(Constants.TENANT_URL)
                + Constants.CDP_URL_V2
                + Constants.ANSI_SQL_URL
                + Constants.SLASH
                + nextBatchId
        );

        Request request = HttpHelper.buildRequest(Constants.GET, url.toString(), null, createHeaders(tokenWithTenantUrl, this.connection.getEnableArrowStream()));
        return getResponse(request);
    }

    public Response getMetadata() throws IOException, SQLException {
        log.info("Getting metadata from CDP query service");
        Map<String, String> tokenWithTenantUrl = getTokenWithTenantUrl();
        StringBuilder url = new StringBuilder(Constants.PROTOCOL + tokenWithTenantUrl.get(Constants.TENANT_URL)
                + Constants.CDP_URL
                + Constants.METADATA_URL);
        Request request = HttpHelper.buildRequest(Constants.GET, url.toString(), null, createHeaders(tokenWithTenantUrl, false));
        return getResponse(request);
    }

    protected OkHttpClient createClient() {
        return new OkHttpClient().newBuilder()
                .readTimeout(Constants.REST_TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(Constants.REST_TIME_OUT, TimeUnit.SECONDS)
                .callTimeout(Constants.REST_TIME_OUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(new MetadataCacheInterceptor())
                .addInterceptor(new RetryInterceptor())
                .build();
    }

    protected Response getResponse(Request request) throws IOException {
        long startTime = System.currentTimeMillis();
        Response response = client.newCall(request).execute();
        long endTime = System.currentTimeMillis();
        log.info("Total time taken to get response for url {} is {} ms", request.url().toString(), endTime - startTime);
        return response;
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

    protected Map<String, String> getTokenWithTenantUrl() throws SQLException {
        if (connection.getToken() != null && TokenHelper.isAlive(connection.getToken())) {
            return TokenHelper.getTokenWithUrl(connection.getToken());
        }
        Token token = TokenHelper.getToken(connection.getClientInfo(), client);
        connection.setToken(token);
        Map<String, String> tokenMap = TokenHelper.getTokenWithUrl(token);
        return tokenMap;
    }
}
