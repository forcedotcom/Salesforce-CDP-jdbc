package com.salesforce.cdp.queryservice.util;

import com.salesforce.a360.queryservice.grpc.v1.AnsiSqlExtractQueryRequest;
import com.salesforce.a360.queryservice.grpc.v1.AnsiSqlExtractQueryResponse;
import com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryStreamRequest;
import com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryStreamResponse;
import com.salesforce.a360.queryservice.grpc.v1.QueryServiceGrpc;
import com.salesforce.cdp.queryservice.core.QueryServiceConnection;
import com.salesforce.cdp.queryservice.interceptors.GrpcInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Slf4j
public class QueryRainbowExecutor extends QueryTokenExecutor{

    private static final int port = 443;
    private static final int timeoutInMin = 5;

    private final ManagedChannel channel;

    public QueryRainbowExecutor(QueryServiceConnection connection) {
        super(connection);
        channel = ManagedChannelBuilder.forAddress(connection.getTenantUrl(), port).build();
    }

    public QueryRainbowExecutor(QueryServiceConnection connection, OkHttpClient client) {
        super(connection, client);
        channel = ManagedChannelBuilder.forAddress(connection.getTenantUrl(), port).build();
    }
    public Iterator<AnsiSqlExtractQueryResponse> executeQuery(String sql) throws IOException, SQLException {
        log.info("Preparing to execute query rainbow query {}", sql);
        Map<String, String> tokenUrl = getTokenWithTenantUrl();
        QueryServiceGrpc.QueryServiceBlockingStub stub = QueryServiceGrpc.newBlockingStub(channel);
        Properties properties = connection.getClientInfo();
        return stub.withDeadlineAfter(timeoutInMin, TimeUnit.MINUTES).withInterceptors(new GrpcInterceptor(tokenUrl, properties)).aniSqlExtractQuery(AnsiSqlExtractQueryRequest.newBuilder().setQuery(sql).build());
    }
}
