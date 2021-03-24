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
import static com.salesforce.cdp.queryservice.util.Messages.METADATA_EXCEPTION;
import com.salesforce.cdp.queryservice.util.QueryExecutor;
import okhttp3.*;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import static com.salesforce.cdp.queryservice.ResponseEnum.*;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class QueryServiceMetadataTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Mock
    private QueryServiceConnection queryServiceConnection;

    @Mock
    private QueryExecutor queryExecutor;

    private QueryServiceMetadata queryServiceMetadata;

    @Before
    public void init() {
        Properties properties = new Properties();
        properties.put(Constants.BASE_URL, "https://mjrgg9bzgy2dsyzvmjrgkmzzg1.c360a.salesforce.com");
        properties.put(Constants.CORETOKEN, "Test_Token");
        doReturn(queryExecutor).when(queryServiceConnection).getQueryExecutor();
        queryServiceMetadata = new QueryServiceMetadata(queryServiceConnection, "https://mjrgg9bzgy2dsyzvmjrgkmzzg1.c360a.salesforce.com", properties);
    }

    @Test
    public void testGetTablesWithInternalServerError() throws IOException, SQLException {
        String jsonString = INTERNAL_SERVER_ERROR.getResponse();
        Response response = new Response.Builder().code(HttpStatus.SC_UNAUTHORIZED).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Unauthorized").
                body(ResponseBody.create(jsonString, MediaType.parse("application/json"))).build();
        doReturn(response).when(queryExecutor).getMetadata();
        exceptionRule.expect(SQLException.class);
        exceptionRule.expectMessage("Internal Server Error");
        queryServiceMetadata.getTables("", "", "", new String [0]);
    }

    @Test
    public void testGetTablesWithSuccess() throws IOException, SQLException {
        String jsonString = TABLE_METADATA.getResponse();
        Response response = new Response.Builder().code(HttpStatus.SC_OK).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Successful").
                body(ResponseBody.create(jsonString, MediaType.parse("application/json"))).build();
        doReturn(response).when(queryExecutor).getMetadata();
        ResultSet resultSet = queryServiceMetadata.getTables("", "", "", new String [0]);
        resultSet.next();
        Assert.assertEquals(resultSet.getString("TABLE_NAME"), "ContactPointEmail__dlm");
        Assert.assertEquals(resultSet.getMetaData().getColumnCount(), 10);
    }

    @Test
    public void testGetColumnsWithNotFound() throws IOException, SQLException {
        String jsonString = NOT_FOUND.getResponse();
        Response response = new Response.Builder().code(HttpStatus.SC_NOT_FOUND).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Not Found").
                body(ResponseBody.create(jsonString, MediaType.parse("application/json"))).build();
        doReturn(response).when(queryExecutor).getMetadata();
        exceptionRule.expect(SQLException.class);
        exceptionRule.expectMessage(METADATA_EXCEPTION);
        queryServiceMetadata.getColumns("", "", "", "");
    }

    @Test
    public void testGetColumnsForTable() throws IOException, SQLException {
        String jsonString = TABLE_METADATA.getResponse();
        Response response = new Response.Builder().code(HttpStatus.SC_OK).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Successful").
                body(ResponseBody.create(jsonString, MediaType.parse("application/json"))).build();
        doReturn(response).when(queryExecutor).getMetadata();
        ResultSet resultSet = queryServiceMetadata.getColumns("", "", "ContactPointEmail__dlm", "");
        while(resultSet.next()) {
            Assert.assertNotNull(resultSet.getString("COLUMN_NAME"));
            Assert.assertEquals(resultSet.getInt("DATA_TYPE"), Types.VARCHAR);
            Assert.assertEquals(resultSet.getString("SQL_DATA_TYPE"), JavaType.STRING.getName());
        }
        Assert.assertEquals(resultSet.getMetaData().getColumnCount(), 24);
    }

    @Test
    public void testGetColumnWithTablePatternNoMatch() throws IOException, SQLException {
        String jsonString = TABLE_METADATA.getResponse();
        Response response = new Response.Builder().code(HttpStatus.SC_OK).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Successful").
                body(ResponseBody.create(jsonString, MediaType.parse("application/json"))).build();
        doReturn(response).when(queryExecutor).getMetadata();
        ResultSet resultSet = queryServiceMetadata.getColumns("", "", "Individual__dlm", "");
        Assert.assertFalse(resultSet.next());
    }


    private Request buildRequest() {
        return new Request.Builder()
                .url("https://mjrgg9bzgy2dsyzvmjrgkmzzg1.c360a.salesforce.com" + Constants.CDP_URL + Constants.ANSI_SQL_URL)
                .method(Constants.POST, RequestBody.create("{test: test}", MediaType.parse("application/json")))
                .build();
    }
}
