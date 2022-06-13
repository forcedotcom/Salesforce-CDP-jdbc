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
import com.salesforce.cdp.queryservice.model.Token;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.FailsafeException;
import net.jodah.failsafe.RetryPolicy;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Slf4j
public class QueryGrpcExecutor {

    // TODO: check unused imports
    private static final Integer DEFAULT_MAX_RETRY = 3;
    private static final String DEFAULT_MAX_RETRY_STR = DEFAULT_MAX_RETRY.toString();
    private static final ManagedChannel DEFAULT_CHANNEL;
    // TODO: need client too here for the token

    static {
        // TODO:
        // set timeouts - idle timeout, total timeout, keepalive timeout
        // set retry handling
        // check url for diff env.
        DEFAULT_CHANNEL = ManagedChannelBuilder.forAddress("localhost", 7020)
                .maxRetryAttempts(3) // use this instead of retryinterceptor?
//                .idleTimeout()
//                .keepAliveTimeout()
                .usePlaintext() // TODO: ssl?
                .build();
    }

    private final QueryServiceConnection connection;
    private final ManagedChannel channel;

    public QueryGrpcExecutor(QueryServiceConnection connection) {
        this(connection, DEFAULT_CHANNEL);
    }

    public QueryGrpcExecutor(QueryServiceConnection connection, ManagedChannel channel) {
        this.connection = connection;
        this.channel = channel;
    }

    public Iterator<AnsiSqlQueryStreamResponse> executeQuery(String sql, Optional<Integer> limit, Optional<Integer> offset, Optional<String> orderby) throws IOException, SQLException {
        // TODO: check limit, offset, orderby not used.
        // TODO: test retry
        log.info("Preparing to execute query {}", sql);
       // Map<String, String> tokenWithTenantUrl = getTokenWithTenantUrl(); // TODO: extend queryExecutor?

        QueryServiceGrpc.QueryServiceBlockingStub stub = QueryServiceGrpc.newBlockingStub(channel);
        Properties properties = connection.getClientInfo();
        // TODO: move timeout to config
        Iterator<AnsiSqlQueryStreamResponse> responseIterator = stub.withDeadlineAfter(5, TimeUnit.MINUTES).withInterceptors(new GrpcInterceptor("authToken", properties)).ansiSqlQueryStream(AnsiSqlQueryStreamRequest.newBuilder().setQuery(sql).setTenantId("a360/falcondev/4e5a4e98240a46ec891a6425429318bd").build());
        // channel shutdown? - check qs for best practices

        return responseIterator;
    }
}
