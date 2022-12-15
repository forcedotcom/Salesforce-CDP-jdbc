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
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.sql.SQLException;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.replace;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;

@RunWith(PowerMockRunner.class)
@PrepareForTest({QueryServiceConnection.class})
@PowerMockIgnore({"jdk.internal.reflect.*"})
public class QueryServiceConnectionTest {

    private String privateKey = "-----BEGIN PRIVATE KEY-----\n" +
            "Dummy.Private.Key\n" +
            "-----END PRIVATE KEY-----";

    @Test
    @DisplayName("Verify Connection creation and initialization")
    public void testConnectionCreation() throws Exception {
        suppress(method(QueryServiceConnection.class, "isValid"));

        String serverUrl = "jdbc:queryService-jdbc:mysample://something.my.salesforce.com/";
        Properties properties = new Properties();
        properties.put(Constants.USER_NAME, "test-user");
        properties.put(Constants.USER, "test-user-12");

        QueryServiceConnection connection = spy(new QueryServiceConnection(serverUrl, properties));
        String rootUrl = Whitebox.getInternalState(connection, "serviceRootUrl");
        assertThat(rootUrl).isEqualTo("mysample://something.my.salesforce.com");

        assertThat(properties.getProperty(Constants.LOGIN_URL)).isEqualTo("mysample://something.my.salesforce.com");
        assertThat(properties.getProperty(Constants.USER_NAME)).isEqualTo("test-user");

        boolean isStreamEnabled = Whitebox.getInternalState(connection, "enableArrowStream");
        assertThat(isStreamEnabled).isFalse();

        // test with arrowStream enabled
        serverUrl = "jdbc:queryService-jdbc:mysample://something.na45.test1.pc-rnd.salesforce.com/";
        properties.clear();
        properties.put(Constants.USER, "test-user-12");
        properties.put(Constants.ENABLE_ARROW_STREAM, "true");

        connection = spy(new QueryServiceConnection(serverUrl, properties));
        rootUrl = Whitebox.getInternalState(connection, "serviceRootUrl");
        assertThat(rootUrl).isEqualTo("mysample://something.na45.test1.pc-rnd.salesforce.com");

        assertThat(properties.getProperty(Constants.LOGIN_URL)).isEqualTo("mysample://something.na45.test1.pc-rnd.salesforce.com");
        assertThat(properties.getProperty(Constants.USER_NAME)).isEqualTo("test-user-12");

        isStreamEnabled = Whitebox.getInternalState(connection, "enableArrowStream");
        assertThat(isStreamEnabled).isTrue();
    }

    @Test
    @DisplayName("Verify Service root URL extraction")
    public void testServiceURL() throws Exception {
        String serverUrl = "jdbc:queryService-jdbc:mysample://something.my.salesforce.com/";
        String serviceRootUrl = QueryServiceConnection.getServiceRootUrl(serverUrl);
        assertThat(serviceRootUrl).isEqualTo("mysample://something.my.salesforce.com");

        Throwable ex = catchThrowableOfType(() -> {
            QueryServiceConnection.getServiceRootUrl("jdbc:querservice:mysample://something.my.salesforce.com/");
        }, SQLException.class);
        assertThat(ex).isInstanceOf(SQLException.class);
        assertThat(ex.getMessage()).contains("url is specified with invalid datasource");
    }

    @Test
    @DisplayName("Verify connection setup - username/password flow")
    public void testIsValid() throws SQLException {
        replace(MemberMatcher.method(QueryServiceConnection.class, "isValid"))
                .with((o, m, args) -> {return true;});

        String serverUrl = "jdbc:queryService-jdbc:mysample://something.my.salesforce.com/";
        Properties properties = new Properties();
        properties.put(Constants.USER_NAME, "test-user");
        properties.put(Constants.USER, "test-user-12");

        QueryServiceConnection connection = spy(new QueryServiceConnection(serverUrl, properties));
        doCallRealMethod().when(connection).isValid(anyInt());
        QueryServicePreparedStatement preparedStatement = mock(QueryServicePreparedStatement.class);
        doReturn(preparedStatement).when(connection).prepareStatement(anyString());
        doReturn(true).when(preparedStatement).execute();

        assertThat(connection.isValid(10)).isTrue();

        doThrow(new SQLException()).when(preparedStatement).execute();
        Throwable ex = catchThrowableOfType(() -> {
            connection.isValid(10);
        }, SQLException.class);
        assertThat(ex).isInstanceOf(SQLException.class);

        // close connection
        connection.close();
        assertThat(connection.isValid(10)).isFalse();
    }

    @Test
    @DisplayName("Verify connection setup - key pair auth flow")
    public void testIsValidKeyPairAuth() throws SQLException {
        replace(MemberMatcher.method(QueryServiceConnection.class, "isValid"))
                .with((o, m, args) -> {return true;});

        String serverUrl = "jdbc:queryService-jdbc:mysample://something.my.salesforce.com/";
        Properties properties = new Properties();
        properties.put(Constants.USER_NAME, "test-user");
        properties.put(Constants.CLIENT_ID, "somexxxxkey");
        properties.put(Constants.PRIVATE_KEY, privateKey);

        QueryServiceConnection connection = spy(new QueryServiceConnection(serverUrl, properties));
        doCallRealMethod().when(connection).isValid(anyInt());
        QueryServicePreparedStatement preparedStatement = mock(QueryServicePreparedStatement.class);
        doReturn(preparedStatement).when(connection).prepareStatement(anyString());
        doReturn(true).when(preparedStatement).execute();

        assertThat(connection.isValid(10)).isTrue();

        doThrow(new SQLException()).when(preparedStatement).execute();
        Throwable ex = catchThrowableOfType(() -> {
            connection.isValid(10);
        }, SQLException.class);
        assertThat(ex).isInstanceOf(SQLException.class);

        // close connection
        connection.close();
        assertThat(connection.isValid(10)).isFalse();
    }

    @Test
    @DisplayName("Verify gRPC connection setup - username/password flow")
    public void testGrpcIsValid() throws SQLException {
        replace(MemberMatcher.method(QueryServiceConnection.class, "isValid"))
                .with((o, m, args) -> {return true;});

        String serverUrl = "jdbc:queryService-jdbc:mysample://something.my.salesforce.com/";
        Properties properties = new Properties();
        properties.put(Constants.USER_NAME, "test-user");
        properties.put(Constants.USER, "test-user-12");
        properties.put(Constants.ENABLE_STREAM_FLOW, "true");

        QueryServiceConnection connection = spy(new QueryServiceConnection(serverUrl, properties));
        doCallRealMethod().when(connection).isValid(anyInt());
        QueryServicePreparedStatement preparedStatement = mock(QueryServicePreparedStatement.class);
        doReturn(preparedStatement).when(connection).prepareStatement(anyString());
        doReturn(true).when(preparedStatement).execute();

        assertThat(connection.isValid(10)).isTrue();

        doThrow(new SQLException()).when(preparedStatement).execute();
        Throwable ex = catchThrowableOfType(() -> {
            connection.isValid(10);
        }, SQLException.class);
        assertThat(ex).isInstanceOf(SQLException.class);

        // close connection
        connection.close();
        assertThat(connection.isValid(10)).isFalse();
    }

    @Test
    @DisplayName("Verify gRPC connection setup - username/password flow with fallback")
    public void testGrpcIsValidWithFallback() throws SQLException {
        replace(MemberMatcher.method(QueryServiceConnection.class, "isValid"))
                .with((o, m, args) -> {return true;});

        String serverUrl = "jdbc:queryService-jdbc:mysample://something.my.salesforce.com/";
        Properties properties = new Properties();
        properties.put(Constants.USER_NAME, "test-user");
        properties.put(Constants.USER, "test-user-12");
        properties.put(Constants.ENABLE_STREAM_FLOW, "true");

        QueryServiceConnection connection = spy(new QueryServiceConnection(serverUrl, properties));
        doCallRealMethod().when(connection).isValid(anyInt());
        QueryServicePreparedStatement preparedStatement = mock(QueryServicePreparedStatement.class);
        doReturn(preparedStatement).when(connection).prepareStatement(anyString());

        // 1st time throw exception, 2nd time return true
        doThrow(new SQLException()).doReturn(true).when(preparedStatement).execute();
        assertThat(connection.isValid(10)).isTrue();
    }
}
