package com.salesforce.cdp.queryservice.auth.unpwd;

import com.salesforce.cdp.queryservice.auth.token.CoreToken;
import com.salesforce.cdp.queryservice.auth.TokenExchangeHelper;
import com.salesforce.cdp.queryservice.util.Constants;
import com.salesforce.cdp.queryservice.util.TokenException;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Properties;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;

public class UnPwdTokenFlowTest {

    @Test
    public void testUsernamePasswrdFlow() throws TokenException {
        Properties properties = new Properties();
        properties.put("loginURL", "https://login.salesforce.com");
        properties.put("clientId", "somerandomclientid");
        properties.put("userName", "someusername");
        properties.put("clientSecret", "someclientsecret");
        properties.put(Constants.PD, "somepd");

        UnPwdAuthClient unPwdAuthClient = Mockito.mock(UnPwdAuthClient.class);
        TokenExchangeHelper mockTokenExchangeHelper = Mockito.mock(TokenExchangeHelper.class);
        CoreToken mockToken = new CoreToken();
        mockToken.setAccessToken("some_token");

        Mockito.when(unPwdAuthClient.un_pw_login(anyString(),anyString(),anyObject(),anyString(),anyObject(), anyString())).thenReturn(mockToken);
        UnPwdTokenFlow unPwdTokenFlow = new UnPwdTokenFlow(properties, unPwdAuthClient, mockTokenExchangeHelper);

        CoreToken coreToken = unPwdTokenFlow.getCoreToken();
        Assertions.assertThat(coreToken).isEqualTo(mockToken);
    }

}