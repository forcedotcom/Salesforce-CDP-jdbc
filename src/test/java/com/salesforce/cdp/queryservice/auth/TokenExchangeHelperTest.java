package com.salesforce.cdp.queryservice.auth;

import com.salesforce.cdp.queryservice.auth.token.CoreToken;
import com.salesforce.cdp.queryservice.util.TokenException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Properties;

import static com.salesforce.cdp.queryservice.util.Constants.LOGIN_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class TokenExchangeHelperTest {

    @Test
    public void testInstanceUrlIsUsedForExchange() throws TokenException {
        OkHttpClient mockHttpClient = mock(OkHttpClient.class);
        Properties mockProperties = mock(Properties.class);
        when(mockProperties.getProperty(eq(LOGIN_URL))).thenReturn("https://login.salesforce.com");
        TokenExchangeHelper tokenExchangeHelper = new TokenExchangeHelper(mockProperties, mockHttpClient);

        CoreToken mockCoreToken = mock(CoreToken.class);
        when(mockCoreToken.getInstanceUrl()).thenReturn("https://tenant-instance.com");

        try {
            tokenExchangeHelper.exchangeToken(mockCoreToken);
        } catch (Exception e) {
            // Not worried about the exception as we are checking only
            // whether the instance url is used for exchange
        }

        verify(mockCoreToken, Mockito.times(1)).getInstanceUrl();
        ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);
        verify(mockHttpClient, Mockito.times(1)).newCall(captor.capture());

        assertThat(captor.getValue().url().toString())
                .isEqualTo("https://tenant-instance.com/services/a360/token");
    }

}