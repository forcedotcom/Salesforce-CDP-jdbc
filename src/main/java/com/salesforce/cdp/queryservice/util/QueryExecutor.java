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

    private static final OkHttpClient DEFAULT_CLIENT;

    static {
        DEFAULT_CLIENT = new OkHttpClient().newBuilder()
                .readTimeout(Constants.REST_TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(Constants.REST_TIME_OUT, TimeUnit.SECONDS)
                .callTimeout(Constants.REST_TIME_OUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(new MetadataCacheInterceptor())
                .addInterceptor(new RetryInterceptor())
                .build();
    }

    private final QueryServiceConnection connection;
    private final OkHttpClient client;

    public QueryExecutor(QueryServiceConnection connection) {
        this(connection, DEFAULT_CLIENT);
    }

    public QueryExecutor(QueryServiceConnection connection, OkHttpClient client) {
        // fixme: even though constructor is public currently, it is not possible
        //  for users to specify custom Client as part of connection creation
        this.connection = connection;
        this.client = client;
    }

    public Response executeQuery(String sql, Optional<Integer> limit, Optional<Integer> offset, Optional<String> orderby) throws IOException, SQLException {
        // fixme: preferably, avoid using optional as parameter type, instead specify nullable value
        log.info("Preparing to execute query {}", sql);
        AnsiQueryRequest ansiQueryRequest = AnsiQueryRequest.builder().sql(sql).build();
        RequestBody body = RequestBody.create(MediaType.parse(Constants.JSON_CONTENT), new Gson().toJson(ansiQueryRequest));
        Map<String, String> tokenWithTenantUrl = getTokenWithTenantUrl();
        StringBuilder url = new StringBuilder(Constants.PROTOCOL)
                .append(tokenWithTenantUrl.get(Constants.TENANT_URL))
                .append(Constants.CDP_URL)
                .append(Constants.ANSI_SQL_URL);
        if (limit.isPresent()) {
            url.append(Constants.LIMIT).append(limit.get()).append(Constants.AND);
        }
        if (offset.isPresent()) {
            url.append(Constants.OFFSET).append(offset.get()).append(Constants.AND);
        }
        if (orderby.isPresent()) {
            url.append(Constants.ORDERBY).append(orderby.get());
        }
        Request request = HttpHelper.buildRequest(Constants.POST, url.toString(), body, createHeaders(tokenWithTenantUrl, this.connection.getEnableArrowStream()));
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

    @Deprecated
    protected OkHttpClient createClient() {
        return DEFAULT_CLIENT.newBuilder()
                .build();
    }

    protected Response getResponse(Request request) throws IOException {
        long startTime = System.currentTimeMillis();
        Response response = client.newCall(request).execute();
        long endTime = System.currentTimeMillis();
        log.info("Total time taken to get response for url {} is {} ms", request.url(), endTime - startTime);
        return response;
    }

    private Map<String, String> createHeaders(Map<String, String> tokenWithTenantUrl, boolean enableArrowStream) throws SQLException {
        Properties properties = connection.getClientInfo();
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.AUTHORIZATION, tokenWithTenantUrl.get(Constants.ACCESS_TOKEN));
        headers.put(Constants.CONTENT_TYPE, Constants.JSON_CONTENT);
        if(enableArrowStream) {
            headers.put(Constants.ENABLE_ARROW_STREAM,"true");
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
        try {
            Token token = TokenHelper.getToken(connection.getClientInfo(), client);
            connection.setToken(token);
            return TokenHelper.getTokenWithUrl(token);
        } catch (TokenException e) {
            throw new SQLException(e);
        }
    }
}
