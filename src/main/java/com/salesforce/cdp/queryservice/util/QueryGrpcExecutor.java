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
import com.salesforce.a360.queryservice.grpc.v1.OutputFormat;
import com.salesforce.a360.queryservice.grpc.v1.QueryServiceGrpc;
import com.salesforce.cdp.queryservice.core.QueryServiceConnection;
import com.salesforce.cdp.queryservice.interceptors.GrpcInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
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

    private static ManagedChannel DEFAULT_CHANNEL = null;
    private static final int port = 443;
    private static final int timeoutInMin = 5;
    // No retry on hyper for now. Fallback to v2 call if receive even one failure from hyper.
    private static final int GRPC_MAX_RETRY = 0;

    private final ManagedChannel channel;

    public QueryGrpcExecutor(QueryServiceConnection connection) {
        this(connection, DEFAULT_CHANNEL);
    }

    public QueryGrpcExecutor(QueryServiceConnection connection, ManagedChannel newChannel) {
        super(connection);
        if(newChannel == null) {
            newChannel = getChannel(connection.getTenantUrl());
        }
        this.channel = newChannel;
        if(DEFAULT_CHANNEL == null) {
            DEFAULT_CHANNEL = channel;
        }
    }

    private ManagedChannel getChannel(String tenantUrl) {
        if(tenantUrl!=null) {
            try {
                return ManagedChannelBuilder.forAddress(tenantUrl, port).build();
            } catch (Exception ex) {
                log.error("encountered exception in grpc connection builder ", ex);
            }
        }

        connection.updateStreamFlow(false);
        return channel;
    }

    public Iterator<AnsiSqlQueryStreamResponse> executeQueryWithRetry(String sql) throws IOException, SQLException {
        RetryPolicy<Object> retryPolicy = new RetryPolicy<>()
                .handleIf(this::ifRetryableGrpcCode)
                .onRetry(e -> log.warn("Failure #{}. Retrying.", e.getAttemptCount()))
                .onRetriesExceeded(e -> log.warn("Failed to connect. Max retries exceeded."))
                .withMaxRetries(GRPC_MAX_RETRY);
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

    private boolean ifRetryableGrpcCode(Object failure) {
        if(failure instanceof StatusRuntimeException) {
            final Status.Code code = ((StatusRuntimeException) failure).getStatus().getCode();
            return (Utils.getGrpcRetryStatusCodes().contains(code));
        }
        return false;
    }

    private Iterator<AnsiSqlQueryStreamResponse> executeQuery(String sql) throws IOException, SQLException {
        log.info("Preparing to execute query with gRPC executor {}", sql);
        Map<String, String> tokenWithTenantUrl = getTokenWithTenantUrl();
        QueryServiceGrpc.QueryServiceBlockingStub stub = QueryServiceGrpc.newBlockingStub(channel);
        Properties properties = connection.getClientInfo();
        return stub
            .withDeadlineAfter(timeoutInMin, TimeUnit.MINUTES)
            .withInterceptors(new GrpcInterceptor(tokenWithTenantUrl, properties))
            .ansiSqlQueryStream(
                AnsiSqlQueryStreamRequest
                    .newBuilder()
                    .setQuery(sql)
                    .setOutputFormat(OutputFormat.ARROW)
                    .build());
    }
}
