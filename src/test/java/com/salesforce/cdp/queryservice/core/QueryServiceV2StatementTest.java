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

package com.salesforce.cdp.queryservice.core;

import com.salesforce.cdp.queryservice.ResponseEnum;
import com.salesforce.cdp.queryservice.util.Constants;
import com.salesforce.cdp.queryservice.util.QueryExecutor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({QueryServiceConnection.class})
public class QueryServiceV2StatementTest {

    @Mock
    private QueryServiceConnection queryServiceConnection;

    private QueryExecutor queryExecutor;

    private QueryServiceStatement queryServiceStatement;

    @Before
    public void init() {
        queryExecutor = mock(QueryExecutor.class);
        Mockito.when(queryServiceConnection.isCursorBasedPaginationReq()).thenReturn(true);
        queryServiceStatement = new QueryServiceStatement(queryServiceConnection, ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY) {
            @Override
            protected QueryExecutor createQueryExecutor() {
                return queryExecutor;
            }
        };
    }

    @Test
    public void testExecuteQueryV2WithSuccessfulResponse() throws IOException, SQLException {
        String jsonString = ResponseEnum.QUERY_RESPONSE_V2.getResponse();
        Response response = new Response.Builder().code(HttpStatus.SC_OK).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Successful").
                body(ResponseBody.create(jsonString, MediaType.parse("application/json"))).build();
        doReturn(response).when(queryExecutor).executeQuery(anyString(), anyBoolean(), any(Optional.class), any(Optional.class), any(Optional.class));
        QueryServiceResultSetV2 resultSet = (QueryServiceResultSetV2) queryServiceStatement.executeQuery("select Id__c, FirstName__c from UnifiedIndividual__dlm limit 1");
        int count = 0;
        while (resultSet.next()) {
            Assert.assertNotNull(resultSet.getString(1));
            Assert.assertNotNull(resultSet.getString(2));
            count++;
        }
        Assert.assertEquals(resultSet.getMetaData().getColumnCount(), 2);
        Assert.assertEquals(resultSet.getMetaData().getColumnName(1), "Id__c");
        Assert.assertEquals(resultSet.getMetaData().getColumnName(2), "FirstName__c");
        Assert.assertEquals(resultSet.getMetaData().getColumnType(1), 12);
        Assert.assertEquals(resultSet.getMetaData().getColumnType(2), 12);
        Assert.assertEquals(count, 1);
    }

    @Test
    public void testPaginationV2() throws IOException, SQLException {
        String paginationResponseString = ResponseEnum.PAGINATED_RESPONSE_V2.getResponse();
        Response paginationResponse = new Response.Builder().code(HttpStatus.SC_OK).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Successful").
                body(ResponseBody.create(paginationResponseString, MediaType.parse("application/json"))).build();
        String queryResponseString = ResponseEnum.QUERY_RESPONSE_V2.getResponse();
        Response queryResponse = new Response.Builder().code(HttpStatus.SC_OK).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Successful").
                body(ResponseBody.create(queryResponseString, MediaType.parse("application/json"))).build();
        doReturn(paginationResponse).when(queryExecutor).executeQuery(anyString(), anyBoolean(), any(Optional.class), any(Optional.class), any(Optional.class));
        doReturn(queryResponse).when(queryExecutor).executeNextBatchQuery("f98c7bcd-b1bd-4e8d-b98d-11aabdd6c604");
        QueryServiceStatement queryServiceStatementSpy = Mockito.spy(queryServiceStatement);
        ResultSet resultSet = queryServiceStatementSpy.executeQuery("select Id__c, FirstName__c from UnifiedIndividual__dlm");
        int count = 0;
        while (resultSet.next()) {
            count++;
        }
        Assert.assertEquals(count, 2);
        verify(queryServiceStatementSpy, times(1)).executeQuery(eq("select Id__c, FirstName__c from UnifiedIndividual__dlm"));
        verify(queryServiceStatementSpy, times(1)).executeNextBatchQuery(eq("f98c7bcd-b1bd-4e8d-b98d-11aabdd6c604"));
    }

    @Test
    public void testExecuteQueryV2ForTableauQueries() throws IOException, SQLException {
        String queryResponseString = ResponseEnum.QUERY_RESPONSE_V2.getResponse();
        Response queryResponse = new Response.Builder().code(HttpStatus.SC_OK).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Successful").
                body(ResponseBody.create(queryResponseString, MediaType.parse("application/json"))).build();
        doReturn(queryResponse).when(queryExecutor).executeQuery(anyString(), anyBoolean(), any(Optional.class), any(Optional.class), any(Optional.class));
        doReturn(Constants.TABLEAU_USER_AGENT_VALUE).when(queryServiceConnection).getClientInfo(eq(Constants.USER_AGENT));
        QueryServiceStatement queryServiceStatementSpy = Mockito.spy(queryServiceStatement);
        ResultSet resultSet = queryServiceStatementSpy.executeQuery("select Id__c, FirstName__c from UnifiedIndividual__dlm limit 1");
        int count = 0;
        while (resultSet.next()) {
            count++;
        }
        Assert.assertEquals(count, 1);
        ArgumentCaptor<Optional> captor = ArgumentCaptor.forClass(Optional.class);
        verify(queryExecutor, times(1)).executeQuery(eq("select Id__c, FirstName__c from UnifiedIndividual__dlm limit 1"),  anyBoolean(), any(Optional.class), any(Optional.class), captor.capture());
        Optional<String> value = captor.getValue();
        Assert.assertEquals(value, Optional.empty());
    }

    private Request buildRequest() {
        return new Request.Builder()
                .url("https://mjrgg9bzgy2dsyzvmjrgkmzzg1.c360a.salesforce.com" + Constants.CDP_URL_V2 + Constants.ANSI_SQL_URL + Constants.QUESTION_MARK)
                .method(Constants.POST, RequestBody.create("{test: test}", MediaType.parse("application/json")))
                .build();
    }
}
