package com.salesforce.cdp.queryservice.util;

import okhttp3.*;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import static org.mockito.Mockito.*;

@PrepareForTest({HttpHelper.class, TokenHelper.class})
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("jdk.internal.reflect.*")
public class QueryExecutorTest {

    private QueryExecutor queryExecutor;

    @Before
    public void init() throws IOException, SQLException {
        Properties properties = new Properties();
        properties.put(Constants.BASE_URL, "https://mjrgg9bzgy2dsyzvmjrgkmzzg1.c360a.salesforce.com");
        properties.put(Constants.CORETOKEN, "Test_Token");
        queryExecutor = new QueryExecutor(properties) {
            @Override
            protected OkHttpClient createClient() {
                return mock(OkHttpClient.class);
            }

            @Override
            protected Response getResponse(Request request) throws IOException {
                return new Response.Builder().code(HttpStatus.SC_OK).
                        request(request).protocol(Protocol.HTTP_1_1).
                        message("Successful").build();
            }
        };
        PowerMockito.mockStatic(HttpHelper.class);
        PowerMockito.mockStatic(TokenHelper.class);
        when(TokenHelper.getTokenWithTenantUrl(any(Properties.class), any(OkHttpClient.class))).thenReturn(getTokenWithUrl());
        when(HttpHelper.buildRequest(anyString(), anyString(), any(RequestBody.class), any(Map.class))).thenReturn(buildRequest());
    }

    @Test
    public void testExecuteQuery() throws IOException, SQLException {
        queryExecutor.executeQuery("select FirstName__c from Individual__dlm limit 10", Optional.empty(), Optional.empty(), Optional.empty());
        PowerMockito.verifyStatic();
        HttpHelper.buildRequest(eq(Constants.POST), eq("https://mjrgg9bzgy2dsyzvmjrgkmzzg1.c360a.salesforce.com" + Constants.CDP_URL + Constants.ANSI_SQL_URL), any(RequestBody.class), any(Map.class));
    }

    @Test
    public void testGetMetadata() throws IOException, SQLException {
        queryExecutor.getMetadata();
        PowerMockito.verifyStatic();
        HttpHelper.buildRequest(eq(Constants.GET), eq("https://mjrgg9bzgy2dsyzvmjrgkmzzg1.c360a.salesforce.com" + Constants.CDP_URL + Constants.METADATA_URL), any(RequestBody.class), any(Map.class));
    }

    @Test
    public void testExecuteQueryWithOptionalParams() throws IOException, SQLException {
        queryExecutor.executeQuery("select FirstName__c from Individual__dlm limit 10", Optional.of(10), Optional.of(10), Optional.of("1 ASC"));
        PowerMockito.verifyStatic();
        HttpHelper.buildRequest(eq(Constants.POST), eq("https://mjrgg9bzgy2dsyzvmjrgkmzzg1.c360a.salesforce.com" + Constants.CDP_URL + Constants.ANSI_SQL_URL + "limit=10&offset=10&orderby=1 ASC")
                , any(RequestBody.class), any(Map.class));
    }

    private Map<String, String> getTokenWithUrl() {
        Map<String, String> tokenWithUrlMap = new HashMap<>();
        tokenWithUrlMap.put(Constants.ACCESS_TOKEN, "q1234");
        tokenWithUrlMap.put(Constants.TENANT_URL, "mjrgg9bzgy2dsyzvmjrgkmzzg1.c360a.salesforce.com");
        return tokenWithUrlMap;
    }

    private Request buildRequest() {
        return new Request.Builder()
                .url("https://mjrgg9bzgy2dsyzvmjrgkmzzg1.c360a.salesforce.com" + Constants.CDP_URL + Constants.ANSI_SQL_URL)
                .method(Constants.POST, RequestBody.create("{test: test}", MediaType.parse("application/json")))
                .build();
    }
}
