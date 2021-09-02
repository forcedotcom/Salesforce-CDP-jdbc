package com.salesforce.cdp.queryservice.core;

import com.salesforce.cdp.queryservice.util.Constants;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(PowerMockRunner.class)
public class QueryServiceConnectionTest {

    @Test
    @DisplayName("Verify Connection creation and initialization")
    public void testConnectionCreation() {
        String serverUrl = "jdbc:queryService-jdbc:mysample://something.my.salesforce.com/";
        Properties properties = new Properties();
        properties.put(Constants.USER_NAME, "test-user");
        properties.put(Constants.USER, "test-user-12");

        QueryServiceConnection connection = new QueryServiceConnection(serverUrl, properties);
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
        properties = new Properties();
        properties.put(Constants.USER, "test-user-12");
        properties.put(Constants.ENABLE_ARROW_STREAM, "true");

        connection = new QueryServiceConnection(serverUrl, properties);
        rootUrl = Whitebox.getInternalState(connection, "serviceRootUrl");
        assertThat(rootUrl).isEqualTo("mysample://something.na45.test1.pc-rnd.salesforce.com");

        assertThat(properties.getProperty(Constants.LOGIN_URL)).isEqualTo("mysample://something.na45.test1.pc-rnd.salesforce.com");
        assertThat(properties.getProperty(Constants.CLIENT_ID)).isEqualTo(Constants.NA45_DEFAULT_CLIENT_ID);
        assertThat(properties.getProperty(Constants.CLIENT_SECRET)).isEqualTo(Constants.NA45_DEFAULT_CLIENT_SECRET);
        assertThat(properties.getProperty(Constants.USER_NAME)).isEqualTo("test-user-12");

        isStreamEnabled = Whitebox.getInternalState(connection, "enableArrowStream");
        assertThat(isStreamEnabled).isTrue();
    }
}
