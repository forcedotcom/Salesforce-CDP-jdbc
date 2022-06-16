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

import com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryStreamRequest;
import com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryStreamResponse;
import com.salesforce.a360.queryservice.grpc.v1.QueryServiceGrpc;
import com.salesforce.cdp.queryservice.core.QueryServiceConnection;
import com.salesforce.cdp.queryservice.interceptors.GrpcInterceptor;
import com.salesforce.cdp.queryservice.interceptors.GrpcRetryInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Slf4j
public class QueryGrpcExecutor extends QueryTokenExecutor {

    private static final ManagedChannel DEFAULT_CHANNEL;

    // TODO: check if we need to pass tenantUrl or albUrl per env.?
    private static final String url = "localhost";
    private static final int port = 7020;
    private static final int timeoutInMin = 5;

    static {
        // TODO: set timeouts - idle timeout, total timeout, keepalive timeout
        DEFAULT_CHANNEL = ManagedChannelBuilder.forAddress(url, port)
//                .intercept(new GrpcRetryInterceptor())
//                .idleTimeout()
//                .keepAliveTimeout()
                .usePlaintext() // TODO: ssl?
                .build();
    }

    private final ManagedChannel channel;

    public QueryGrpcExecutor(QueryServiceConnection connection) {
        this(connection, DEFAULT_CHANNEL);
    }

    public QueryGrpcExecutor(QueryServiceConnection connection, ManagedChannel channel) {
        super(connection);
        this.channel = channel;
    }

    public Iterator<AnsiSqlQueryStreamResponse> executeQuery(String sql) throws IOException, SQLException {
        log.info("Preparing to execute query {}", sql);
         Map<String, String> tokenWithTenantUrl = getTokenWithTenantUrl();
         StringBuilder tenantUrl = (new StringBuilder("https://")).append((String)tokenWithTenantUrl.get("tenantUrl"));

        QueryServiceGrpc.QueryServiceBlockingStub stub = QueryServiceGrpc.newBlockingStub(channel);
        Properties properties = connection.getClientInfo();
        // TODO: check here on how to use tenantUrl on channel
        // TODO: hardcoded tenantId would go away.
        return stub.withDeadlineAfter(timeoutInMin, TimeUnit.MINUTES).withInterceptors(new GrpcInterceptor("authToken", properties)).ansiSqlQueryStream(AnsiSqlQueryStreamRequest.newBuilder().setQuery(sql).setTenantId("a360/falcondev/4e5a4e98240a46ec891a6425429318bd").build());
    }
}
