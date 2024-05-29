package com.salesforce.cdp.queryservice.auth.jwt;

import com.salesforce.cdp.queryservice.auth.token.CoreToken;
import com.salesforce.cdp.queryservice.auth.TokenExchangeHelper;
import com.salesforce.cdp.queryservice.util.TokenException;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Properties;

import static org.mockito.Matchers.anyString;

public class JwtTokenFlowTest {

    @Test
    public void testJwtCoreTokenProvider() throws TokenException {
        Properties properties = new Properties();
        properties.put("loginURL", "https://login.salesforce.com");
        properties.put("clientId", "somerandomclientid");
        properties.put("userName", "someusername");
        properties.put("privateKey", "someprivatekey");
        JwtLoginClient mockJwtLoginClient = Mockito.mock(JwtLoginClient.class);
        TokenExchangeHelper mockTokenExchangeHelper = Mockito.mock(TokenExchangeHelper.class);
        CoreToken mockToken = new CoreToken();
        mockToken.setAccessToken("access_token");
        Mockito.when(mockJwtLoginClient.keyPairAuthLogin(anyString(),anyString(),anyString(),anyString(),anyString(),anyString()))
                .thenReturn(mockToken);
        JwtTokenFlow jwtCoreTokenProvider = new JwtTokenFlow(properties, mockJwtLoginClient, mockTokenExchangeHelper);
        CoreToken coreToken = jwtCoreTokenProvider.getCoreToken();
        Assertions.assertThat(coreToken).isEqualTo(mockToken);
    }
}