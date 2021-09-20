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

import com.salesforce.cdp.queryservice.core.QueryServiceConnection;
import com.salesforce.cdp.queryservice.model.Token;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
@PowerMockIgnore({"jdk.internal.reflect.*", "javax.net.ssl.*"})
public class QueryExecutorTest {

    private QueryExecutor queryExecutor;

    @Mock
    private QueryServiceConnection connection;

    @Before
    public void init() throws Exception {
        Properties properties = new Properties();
        properties.put(Constants.BASE_URL, "https://mjrgg9bzgy2dsyzvmjrgkmzzg1.c360a.salesforce.com");
        properties.put(Constants.CORETOKEN, "Test_Token");
        doReturn(properties).when(connection).getClientInfo();
        doNothing().when(connection).setToken(any());
        queryExecutor = new QueryExecutor(connection) {
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
        when(TokenHelper.getToken(any(Properties.class), any(OkHttpClient.class))).thenReturn(getToken());
        when(TokenHelper.getTokenWithUrl(any(Token.class))).thenReturn(getTokenWithUrl());
        when(HttpHelper.buildRequest(anyString(), anyString(), any(RequestBody.class), any(Map.class))).thenReturn(buildRequest());
    }

    @Test
    public void testExecuteQuery() throws IOException, SQLException {
        queryExecutor.executeQuery("select FirstName__c from Individual__dlm limit 10", false, Optional.empty(), Optional.empty(), Optional.empty());
        PowerMockito.verifyStatic();
        HttpHelper.buildRequest(eq(Constants.POST), eq("https://mjrgg9bzgy2dsyzvmjrgkmzzg1.c360a.salesforce.com" + Constants.CDP_URL + Constants.ANSI_SQL_URL + Constants.QUESTION_MARK), any(RequestBody.class), any(Map.class));
    }

    @Test
    public void testGetMetadata() throws IOException, SQLException {
        queryExecutor.getMetadata();
        PowerMockito.verifyStatic();
        HttpHelper.buildRequest(eq(Constants.GET), eq("https://mjrgg9bzgy2dsyzvmjrgkmzzg1.c360a.salesforce.com" + Constants.CDP_URL + Constants.METADATA_URL), any(RequestBody.class), any(Map.class));
    }

    @Test
    public void testExecuteQueryWithOptionalParams() throws IOException, SQLException {
        queryExecutor.executeQuery("select FirstName__c from Individual__dlm limit 10", false, Optional.of(10), Optional.of(10), Optional.of("1 ASC"));
        PowerMockito.verifyStatic();
        HttpHelper.buildRequest(eq(Constants.POST), eq("https://mjrgg9bzgy2dsyzvmjrgkmzzg1.c360a.salesforce.com" + Constants.CDP_URL + Constants.ANSI_SQL_URL + Constants.QUESTION_MARK + "limit=10&offset=10&orderby=1 ASC")
                , any(RequestBody.class), any(Map.class));
    }

    private Token getToken() {
        Token token = new Token();
        token.setAccess_token("q1234");
        token.setInstance_url("mjrgg9bzgy2dsyzvmjrgkmzzg1.c360a.salesforce.com");
        return token;
    }

    private Map<String, String> getTokenWithUrl() {
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(Constants.ACCESS_TOKEN, "q1234");
        tokenMap.put(Constants.TENANT_URL, "mjrgg9bzgy2dsyzvmjrgkmzzg1.c360a.salesforce.com");
        return tokenMap;
    }

    private Request buildRequest() {
        return new Request.Builder()
                .url("https://mjrgg9bzgy2dsyzvmjrgkmzzg1.c360a.salesforce.com" + Constants.CDP_URL + Constants.ANSI_SQL_URL + Constants.QUESTION_MARK)
                .method(Constants.POST, RequestBody.create("{test: test}", MediaType.parse("application/json")))
                .build();
    }
}
