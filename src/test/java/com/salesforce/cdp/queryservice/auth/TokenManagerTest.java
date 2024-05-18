package com.salesforce.cdp.queryservice.auth;

import com.salesforce.cdp.queryservice.util.TokenException;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Properties;

import static org.mockito.Matchers.any;

public class TokenManagerTest {

    @Test
    public void testTokenManagerCoreTokenWithUnPasswordFlow() throws TokenException {
        Properties properties = new Properties();
        properties.put("loginURL", "https://login.salesforce.com");
        properties.put("clientId", "somerandomclientid");
        properties.put("userName", "someusername");
        properties.put("clientSecret", "someclientsecret");

        TokenExchangeHelper tokenExchangeHelper = Mockito.mock(TokenExchangeHelper.class);
        OffcoreToken mockOffcoreToken = new OffcoreToken();
        mockOffcoreToken.setAccessToken("offcore_access_token");
        TokenProvider tokenProvider = Mockito.mock(TokenProvider.class);
        CoreToken mockCoreToken = new CoreToken();
        mockCoreToken.setAccessToken("access_token");
        Mockito.when(tokenProvider.getCoreToken()).thenReturn(mockCoreToken);
        Mockito.when(tokenProvider.getOffcoreToken()).thenReturn(mockOffcoreToken);
        TokenManager tokenManager = new TokenManager(tokenProvider);

        CoreToken coreToken = tokenManager.getCoreToken();
        Assertions.assertThat(coreToken).isEqualTo(mockCoreToken);
        OffcoreToken offcoreToken = tokenManager.getOffcoreToken();
        Assertions.assertThat(offcoreToken).isEqualTo(mockOffcoreToken);


    }

}