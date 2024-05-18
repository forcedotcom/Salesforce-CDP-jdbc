package com.salesforce.cdp.queryservice.auth.jwt;

import com.salesforce.cdp.queryservice.auth.CoreToken;
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
        CoreToken mockToken = new CoreToken();
        mockToken.setAccessToken("access_token");
        Mockito.when(mockJwtLoginClient.keyPairAuthLogin(anyString(),anyString(),anyString(),anyString(),anyString(),anyString()))
                .thenReturn(mockToken);
        JwtTokenFlow jwtCoreTokenProvider = new JwtTokenFlow(properties, mockJwtLoginClient);
        CoreToken coreToken = jwtCoreTokenProvider.getCoreToken();
        Assertions.assertThat(coreToken).isEqualTo(mockToken);
    }
}