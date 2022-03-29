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
            "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDEd+ersBuV4tAv\n" +
            "Dx3TzZsgtHf83+gfgea7TdDsv+ANiSwEDohgvrCETYbLvezul/t8EyrGwu9Ob7FW\n" +
            "oqaNgI5ZXN8ykdI+G0XkCEUIQL5+iH1UXceSh35xqdDLozgDvqnPp6CtsoTlQVnt\n" +
            "F9B/7UrzLz40DEH+/aihlLKd/0+n/mabO0vf9ORCas/h8923bMdmg05uQda4vwaF\n" +
            "RoN8JqSYaE0m+pbHFhxYoR9sV43S1X2T1d1gqsa5GJZ7x5C5Xc8fcargh/E/kV6m\n" +
            "BTYKBb17mqWnxmztgbT5cwVGbAE3A5L04jdCED0jPCdouGN99vc+i0pSKwH+zIfO\n" +
            "Hqqjk91nAgMBAAECggEAPMr5dcfFkWigkJ811I8ffEw7gJTsJ6uzcKvQhSGOO8IB\n" +
            "S7QPgRggWKAAoNTBFscSez8XEm/JStUG487qiIiKA57YNbanfq5Dvx7L9ZTLHS4w\n" +
            "0cU+9tlUR+mLASgdMhNySh4NexHtS18ga1veGWectIkez04nsbZd0rnHV1pkFI/5\n" +
            "ek1cSuJ9L5IyNiLx8+K7x2msEMuJDG6Motmo6MTm39xkM8w9yLCJG3F7tRR/eD7+\n" +
            "cVGrdMVNiaxZY/SCYzcGpfwzfTX16o5a5osnB/11RXNLKnXg/dk2+3dQxPlXXu/a\n" +
            "rz5ca1dM8U9rR5K+fNJNWitiUm9zDzZEZ1o8uoxRiQKBgQDz43loFciVnrsiH3Gh\n" +
            "NiCvdgZh3laUzz8C+HW4xFwYqbL1XPt4Sv21Fu4a4gK+rjrXqz7H1Nf2toyfxtH7\n" +
            "QhrZwC6+vAtIvGstWVaNrmLA1gOwyJ0ar/nwBfBGdaLoU6EnX/YpEnfiTfng8yuH\n" +
            "0JvoMJceF0LOHTikDUDht/OSPQKBgQDOOZVlS5IeNp+i8xVhHdMsZqEcWbEWqb3o\n" +
            "cQbTPwvHXma4cqVue7ZrUmFwsd3ilkbEJsYpWPcAWN7QQ2VgcvBf6uvmYX4YhIJX\n" +
            "XKNgk6FN+9d09PZaVfE+f4TDIRjtJ9DVaBgURj72H9/J7K6AAgbeKzglcNObWdQs\n" +
            "5qq1IPyccwKBgADHG+8CCsa3X99m/ETIWGhW1wRe4iXNV2UaB74UGjsV53Uy27Zx\n" +
            "fseiEBZT3DBhe9yONkAK5LlrsZ0c1DSZ7F3/Z+bB0MNlnm3hmA4RnU0CIbbhnOal\n" +
            "4wUp76851tAo3B21B6Lv5SP6na5i+CORvb2K0iCNcHAZ1cFoLWnK3WL9AoGAcSrw\n" +
            "bNH3sVTQbZ9v0AeJ5we6yc/+ei1T5caAtFQYpqOLQxTG68Y/6M0gY7N3y+wjkWil\n" +
            "vfLwOOSMAUW60B7DAh/srFQ72kB9NmvDzC+3iQ/2wFvdBN28sUtRE7OJ9jqvQy0I\n" +
            "abfSvUXojOqxJ9X05t5YxVMRDGNTKAC9FQCxHzkCgYANLL6ypEx2khqurXAuVa1/\n" +
            "eOWbpzftHQO11Fy/dAbOd9Uyl5ks1F2ljzQCPJzSwwJ3fKHQfdDHQYaVRqtAKbG8\n" +
            "aqObOSrtr2RLYfWF9M+pD6JjbMuErohnOuqYEX7cARhSpP0O6f/UMhm/uvgiwSGm\n" +
            "nkxuHk2xwyIHybqlbQOpQw==\n" +
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
        assertThat(properties.getProperty(Constants.CLIENT_ID)).isEqualTo(Constants.PROD_DEFAULT_CLIENT_ID);
        assertThat(properties.getProperty(Constants.CLIENT_SECRET)).isEqualTo(Constants.PROD_DEFAULT_CLIENT_SECRET);
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
        assertThat(properties.getProperty(Constants.CLIENT_ID)).isEqualTo(Constants.NA45_DEFAULT_CLIENT_ID);
        assertThat(properties.getProperty(Constants.CLIENT_SECRET)).isEqualTo(Constants.NA45_DEFAULT_CLIENT_SECRET);
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
    @DisplayName("Verify Addition of Client Secrets")
    public void testAddingClientSecrets() throws Exception {
        Properties properties = new Properties();
        String serverUrl = "mysample://something.na46.test1.pc-rnd.salesforce.com";

        properties.put(Constants.USER, "test-user-12");

        assertThat(properties.size()).isEqualTo(1);
        QueryServiceConnection.addClientSecretsIfRequired(serverUrl, properties);
        assertThat(properties.getProperty(Constants.USER_NAME)).isEqualTo("test-user-12");
        assertThat(properties.getProperty(Constants.CLIENT_ID)).isEqualTo(Constants.NA46_DEFAULT_CLIENT_ID);
        assertThat(properties.getProperty(Constants.CLIENT_SECRET)).isEqualTo(Constants.NA46_DEFAULT_CLIENT_SECRET);
        assertThat(properties.size()).isEqualTo(4);

        // case when no username is present
        properties.clear();
        QueryServiceConnection.addClientSecretsIfRequired(serverUrl, properties);
        assertThat(properties.size()).isEqualTo(0);

        // case when only username is present
        properties.clear();
        properties.put(Constants.USER_NAME, "test-user");
        QueryServiceConnection.addClientSecretsIfRequired(serverUrl, properties);
        assertThat(properties.size()).isEqualTo(3);

        // case when username, clientId/clientSecret are present
        properties.clear();
        properties.put(Constants.USER_NAME, "test-user");
        properties.put(Constants.CLIENT_ID, "bleh");
        QueryServiceConnection.addClientSecretsIfRequired(serverUrl, properties);
        assertThat(properties.size()).isEqualTo(2);

        properties.put(Constants.CLIENT_SECRET, "secret");
        QueryServiceConnection.addClientSecretsIfRequired(serverUrl, properties);
        assertThat(properties.size()).isEqualTo(3);

        // case when username and privateKey are present
        properties.clear();
        properties.put(Constants.USER_NAME, "test-user");
        properties.put(Constants.PRIVATE_KEY, privateKey);
        QueryServiceConnection.addClientSecretsIfRequired(serverUrl, properties);
        assertThat(properties.size()).isEqualTo(2);

        Throwable ex = catchThrowableOfType(() -> {
            String url = "mysample://something.na46.test1.pc-rnd.example.com";
            Properties configs = new Properties();
            configs.put(Constants.USER, "Test-user-12");
            QueryServiceConnection.addClientSecretsIfRequired(url, configs);
        }, SQLException.class);
        assertThat(ex).isInstanceOf(SQLException.class);
        assertThat(ex.getMessage()).contains("specified url didn't match any existing envs");
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
        properties.put(Constants.CLIENT_ID, Constants.PROD_DEFAULT_CLIENT_ID);
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
}
