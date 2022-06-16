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
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.FailsafeException;
import net.jodah.failsafe.RetryPolicy;

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

    public Iterator<AnsiSqlQueryStreamResponse> executeQueryWithRetry(String sql) throws IOException, SQLException {
        // TODO: retry case wise.
        RetryPolicy<Object> retryPolicy = new RetryPolicy<>()
                .handle(StatusRuntimeException.class)
//                .handleIf((res, ex) -> {System.out.print(res); return true;})
                .onRetry(e -> log.warn("Failure #{}. Retrying.", e.getAttemptCount()))
                .onRetriesExceeded(e -> log.warn("Failed to connect. Max retries exceeded."))
                .withMaxRetries(DEFAULT_MAX_RETRY);
        try {
            return Failsafe.with(retryPolicy)
                    .get(() -> {
                        Iterator<AnsiSqlQueryStreamResponse> response = executeQuery(sql);
                        // This checks if there is failure in first chunk itself.
                        // NOTE: failure in later chunks is not handled intentionally here.
                        // as in that case, we expect a new request from client.
                        response.hasNext();
                        return response;
                    });
        } catch (FailsafeException e) {
            if (e.getCause() != null) {
                throw new SQLException(e.getCause().getMessage(), e.getCause());
            }
            throw new SQLException(e);
        }
    }

    private Iterator<AnsiSqlQueryStreamResponse> executeQuery(String sql) throws IOException, SQLException {
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
