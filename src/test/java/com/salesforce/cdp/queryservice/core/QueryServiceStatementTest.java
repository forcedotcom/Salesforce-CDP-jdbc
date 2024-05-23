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

import com.salesforce.cdp.queryservice.util.Constants;
import static com.salesforce.cdp.queryservice.util.Messages.QUERY_EXCEPTION;
import com.salesforce.cdp.queryservice.util.QueryExecutor;
import com.salesforce.cdp.queryservice.ResponseEnum;
import okhttp3.*;
import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.Assert;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QueryServiceStatementTest {

    @Mock
    private QueryServiceConnection queryServiceConnection;

    private QueryExecutor queryExecutor;

    private QueryServiceStatement queryServiceStatement;

    @Before
    public void init() {
        queryExecutor = mock(QueryExecutor.class);
        queryServiceStatement = new QueryServiceStatement(queryServiceConnection, ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY) {
            @Override
            protected QueryExecutor createQueryExecutor() {
                return queryExecutor;
            }
        };
    }

    @Test
    public void testExecuteQueryWithFailedResponse() throws SQLException, IOException {
        String jsonString = ResponseEnum.UNAUTHORIZED.getResponse();
        Response response = new Response.Builder().code(HttpStatus.SC_UNAUTHORIZED).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Unauthorized").
                body(ResponseBody.create(jsonString, MediaType.parse("application/json"))).build();
        doReturn(response).when(queryExecutor).executeQuery(anyString(), anyBoolean(), any(Optional.class), any(Optional.class), any(Optional.class));

        Throwable ex = catchThrowableOfType(() -> {
            queryServiceStatement.executeQuery("select FirstName__c from Individual__dlm limit 10");
        }, SQLException.class);
        Assertions.assertThat(ex.getMessage()).contains("Failed to get the response for the query. Please try again.");
    }

    @Test
    public void testExecuteQueryWithSuccessfulResponse() throws IOException, SQLException {
        String jsonString = ResponseEnum.QUERY_RESPONSE.getResponse();
        Response response = new Response.Builder().code(HttpStatus.SC_OK).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Successful").
                body(ResponseBody.create(jsonString, MediaType.parse("application/json"))).build();
        doReturn(response).when(queryExecutor).executeQuery(anyString(), anyBoolean(), any(Optional.class), any(Optional.class), any(Optional.class));
        ResultSet resultSet = queryServiceStatement.executeQuery("select TelephoneNumber__c from ContactPointPhone__dlm GROUP BY 1");
        int count = 0;
        while (resultSet.next()) {
            Assert.assertNotNull(resultSet.getString(1));
            count++;
        }
        Assert.assertEquals(resultSet.getMetaData().getColumnCount(), 1);
        Assert.assertEquals(resultSet.getMetaData().getColumnName(1), "telephonenumber__c");
        Assert.assertEquals(resultSet.getMetaData().getColumnType(1), 12);
        Assert.assertEquals(count, 2);
    }

    @Test
    public void testExecuteQueryWithNoData() throws IOException, SQLException {
        String jsonString = ResponseEnum.EMPTY_RESPONSE.getResponse();
        Response response = new Response.Builder().code(HttpStatus.SC_OK).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Successful").
                body(ResponseBody.create(jsonString, MediaType.parse("application/json"))).build();
        doReturn(response).when(queryExecutor).executeQuery(anyString(), anyBoolean(), any(Optional.class), any(Optional.class), any(Optional.class));
        ResultSet resultSet = queryServiceStatement.executeQuery("select TelephoneNumber__c from ContactPointPhone__dlm GROUP BY 1");
        Assert.assertFalse(resultSet.next());
    }

    @Test
    public void testExecuteQueryListOfFloatsWithSuccessfulResponse() throws IOException, SQLException {
        String jsonString = ResponseEnum.QUERY_RESPONSE_WITH_LIST_OF_FLOATS.getResponse();
        Response response = new Response.Builder().code(HttpStatus.SC_OK).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Successful").
                body(ResponseBody.create(jsonString, MediaType.parse("application/json"))).build();
        doReturn(response).when(queryExecutor).executeQuery(anyString(), anyBoolean(), any(Optional.class), any(Optional.class), any(Optional.class));
        ResultSet resultSet = queryServiceStatement.executeQuery("select TelephoneNumber__c, VectorEmbedding__c from ContactPointPhone__dlm GROUP BY 1");
        int count = 0;
        while (resultSet.next()) {
            Assert.assertNotNull(resultSet.getString(1));
            count++;
        }
        Assert.assertEquals(2, resultSet.getMetaData().getColumnCount());
        Assert.assertEquals("VectorEmbedding__c", resultSet.getMetaData().getColumnName(2));
        Assert.assertEquals(2003, resultSet.getMetaData().getColumnType(2));
        Assert.assertEquals(count, 2);
    }

    @Test
    public void testExceuteQueryWithIOException() throws IOException, SQLException {
        doThrow(new IOException("IO Exception")).when(queryExecutor).executeQuery(anyString(), anyBoolean(), any(Optional.class), any(Optional.class), any(Optional.class));
        Throwable ex = catchThrowableOfType(() -> {
            queryServiceStatement.executeQuery("select FirstName__c from Individual__dlm limit 10");
        }, SQLException.class);
        Assertions.assertThat(ex.getMessage()).contains(QUERY_EXCEPTION);
    }

    @Test
    public void testPagination() throws IOException, SQLException {
        String paginationResponseString = ResponseEnum.PAGINATION_RESPONSE.getResponse();
        Response paginationResponse = new Response.Builder().code(HttpStatus.SC_OK).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Successful").
                body(ResponseBody.create(paginationResponseString, MediaType.parse("application/json"))).build();
        String queryResponseString = ResponseEnum.QUERY_RESPONSE.getResponse();
        Response queryResponse = new Response.Builder().code(HttpStatus.SC_OK).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Successful").
                body(ResponseBody.create(queryResponseString, MediaType.parse("application/json"))).build();
        doReturn(paginationResponse).doReturn(queryResponse).when(queryExecutor).executeQuery(anyString(), anyBoolean(), any(Optional.class), any(Optional.class), any(Optional.class));
        QueryServiceStatement queryServiceStatementSpy = Mockito.spy(queryServiceStatement);
        ResultSet resultSet = queryServiceStatementSpy.executeQuery("select TelephoneNumber__c from ContactPointPhone__dlm GROUP BY 1");
        int count = 0;
        while (resultSet.next()) {
            count++;
        }
        Assert.assertEquals(count, 4);
        verify(queryServiceStatementSpy, times(2)).executeQuery(eq("select TelephoneNumber__c from ContactPointPhone__dlm GROUP BY 1"));
    }

    @Test
    public void testDataWithMetadataResponse() throws IOException, SQLException {
        String jsonString = ResponseEnum.QUERY_RESPONSE_WITH_METADATA.getResponse();
        Response response = new Response.Builder().code(HttpStatus.SC_OK).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Successful").
                body(ResponseBody.create(jsonString, MediaType.parse("application/json"))).build();
        doReturn(response).when(queryExecutor).executeQuery(anyString(), anyBoolean(), any(Optional.class), any(Optional.class), any(Optional.class));
        ResultSet resultSet = queryServiceStatement.executeQuery("select TelephoneNumber__c from ContactPointPhone__dlm GROUP BY 1");
        int count = 0;
        while (resultSet.next()) {
            Assert.assertNotNull(resultSet.getString(1));
            count++;
        }
        Assert.assertEquals(resultSet.getMetaData().getColumnCount(), 1);
        Assert.assertEquals(resultSet.getMetaData().getColumnName(1), "count_num");
        Assert.assertEquals(resultSet.getMetaData().getColumnTypeName(1), "DECIMAL");
        Assert.assertEquals(count, 1);
    }

    @Test
    public void testQueryResponseWithoutPagination() throws IOException, SQLException {
        String paginationResponseString = ResponseEnum.QUERY_RESPONSE_WITHOUT_DONE_FLAG.getResponse();
        Response paginationResponse = new Response.Builder().code(HttpStatus.SC_OK).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Successful").
                body(ResponseBody.create(paginationResponseString, MediaType.parse("application/json"))).build();
        doReturn(paginationResponse).when(queryExecutor).executeQuery(anyString(), anyBoolean(), any(Optional.class), any(Optional.class), any(Optional.class));
        QueryServiceStatement queryServiceStatementSpy = Mockito.spy(queryServiceStatement);
        ResultSet resultSet = queryServiceStatementSpy.executeQuery("select TelephoneNumber__c from ContactPointPhone__dlm GROUP BY 1");
        int count = 0;
        while (resultSet.next()) {
            count++;
        }
        Assert.assertEquals(count, 2);
        verify(queryServiceStatementSpy, times(1)).executeQuery(eq("select TelephoneNumber__c from ContactPointPhone__dlm GROUP BY 1"));
    }

    @Test
    public void testExecuteQueryForOrderByQueries() throws IOException, SQLException {
        String paginationResponseString = ResponseEnum.QUERY_RESPONSE_WITHOUT_DONE_FLAG.getResponse();
        Response paginationResponse = new Response.Builder().code(HttpStatus.SC_OK).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Successful").
                body(ResponseBody.create(paginationResponseString, MediaType.parse("application/json"))).build();
        doReturn(paginationResponse).when(queryExecutor).executeQuery(anyString(), anyBoolean(), any(Optional.class), any(Optional.class), any(Optional.class));
        QueryServiceStatement queryServiceStatementSpy = Mockito.spy(queryServiceStatement);
        ResultSet resultSet = queryServiceStatementSpy.executeQuery("select TelephoneNumber__c from ContactPointPhone__dlm GROUP BY 1 ORDER BY TelephoneNumber__c");
        int count = 0;
        while (resultSet.next()) {
            count++;
        }
        Assert.assertEquals(count, 2);
        ArgumentCaptor<Optional> captor = ArgumentCaptor.forClass(Optional.class);
        verify(queryExecutor, times(1)).executeQuery(eq("select TelephoneNumber__c from ContactPointPhone__dlm GROUP BY 1 ORDER BY TelephoneNumber__c"), anyBoolean(), any(Optional.class), any(Optional.class), captor.capture());
        Optional<String> value = captor.getValue();
        Assert.assertFalse(value.isPresent());
    }

    @Test
    public void testExecuteQueryForTableauQueries() throws IOException, SQLException {
        String paginationResponseString = ResponseEnum.QUERY_RESPONSE_WITHOUT_DONE_FLAG.getResponse();
        Response paginationResponse = new Response.Builder().code(HttpStatus.SC_OK).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Successful").
                body(ResponseBody.create(paginationResponseString, MediaType.parse("application/json"))).build();
        doReturn(paginationResponse).when(queryExecutor).executeQuery(anyString(), anyBoolean(), any(Optional.class), any(Optional.class), any(Optional.class));
        doReturn(Constants.TABLEAU_USER_AGENT_VALUE).when(queryServiceConnection).getClientInfo(eq(Constants.USER_AGENT));
        QueryServiceStatement queryServiceStatementSpy = Mockito.spy(queryServiceStatement);
        ResultSet resultSet = queryServiceStatementSpy.executeQuery("select TelephoneNumber__c from ContactPointPhone__dlm GROUP BY 1");
        int count = 0;
        while (resultSet.next()) {
            count++;
        }
        Assert.assertEquals(count, 2);
        ArgumentCaptor<Optional> captor = ArgumentCaptor.forClass(Optional.class);
        verify(queryExecutor, times(1)).executeQuery(eq("select TelephoneNumber__c from ContactPointPhone__dlm GROUP BY 1"),  anyBoolean(), any(Optional.class), any(Optional.class), captor.capture());
        Optional<String> value = captor.getValue();
        String val = value.get();
        Assert.assertEquals(val, "1 ASC");
    }

    private Request buildRequest() {
        return new Request.Builder()
                .url("https://mjrgg9bzgy2dsyzvmjrgkmzzg1.c360a.salesforce.com" + Constants.CDP_URL + Constants.ANSI_SQL_URL + Constants.QUESTION_MARK)
                .method(Constants.POST, RequestBody.create("{test: test}", MediaType.parse("application/json")))
                .build();
    }
}
