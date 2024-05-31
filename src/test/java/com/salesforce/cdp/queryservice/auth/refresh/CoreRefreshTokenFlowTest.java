package com.salesforce.cdp.queryservice.auth.refresh;

import com.salesforce.cdp.queryservice.auth.token.CoreToken;
import com.salesforce.cdp.queryservice.auth.token.OffcoreToken;
import com.salesforce.cdp.queryservice.auth.TokenExchangeHelper;
import com.salesforce.cdp.queryservice.util.TokenException;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Calendar;
import java.util.Properties;

import static org.mockito.Matchers.anyString;

public class CoreRefreshTokenFlowTest {

    @Test
    public void testCoreTokenWithRefreshTokenTest() throws TokenException {
        Properties properties = new Properties();
        properties.put("loginURL", "https://login.salesforce.com");
        properties.put("clientId", "somerandomclientid");
        properties.put("clientSecret", "clientSecret");
        properties.put("refreshToken", "refreshToken");

        RefreshTokenClient refreshTokenClient = Mockito.mock(RefreshTokenClient.class);
        CoreToken mockToken = new CoreToken();
        mockToken.setRefreshToken("some_refresh_token");
        mockToken.setAccessToken("some_access_token");
        Mockito.when(refreshTokenClient.getCoreToken(anyString(),anyString(),anyString(),anyString())).thenReturn(mockToken);
        TokenExchangeHelper tokenExchangeHelper = Mockito.mock(TokenExchangeHelper.class);
        RefreshTokenFlow coreRefreshTokenFlow = new RefreshTokenFlow(properties, refreshTokenClient, tokenExchangeHelper);
        CoreToken coreToken = coreRefreshTokenFlow.getCoreToken();
        Assertions.assertThat(coreToken).isEqualTo(mockToken);
        Assertions.assertThat(properties.get("refreshToken")).isEqualTo("some_refresh_token");
    }

    @Test
    public void testOffcoreTokenWithRefreshTokenTest() throws TokenException {
        Properties properties = new Properties();
        properties.put("loginURL", "https://login.salesforce.com");
        properties.put("clientId", "somerandomclientid");
        properties.put("clientSecret", "clientSecret");
        properties.put("coreToken", "coreToken");
        properties.put("refreshToken", "refreshToken");

        RefreshTokenClient refreshTokenClient = Mockito.mock(RefreshTokenClient.class);
        CoreToken mockToken = new CoreToken();
        mockToken.setRefreshToken("some_refresh_token");
        mockToken.setAccessToken("some_access_token");
        Mockito.when(refreshTokenClient.getCoreToken(anyString(),anyString(),anyString(),anyString())).thenReturn(mockToken);
        TokenExchangeHelper tokenExchangeHelper = Mockito.mock(TokenExchangeHelper.class);
        OffcoreToken mockOffcoreToken = new OffcoreToken();
        mockOffcoreToken.setAccessToken("someaccesstoken");
        Calendar expiryTime = Calendar.getInstance();
        expiryTime.add(Calendar.HOUR, 24);
        mockOffcoreToken.setExpireTime(expiryTime);
        Mockito.when(tokenExchangeHelper.exchangeToken(Mockito.any())).thenReturn(mockOffcoreToken);
        RefreshTokenFlow coreRefreshTokenFlow = new RefreshTokenFlow(properties, refreshTokenClient, tokenExchangeHelper);
        OffcoreToken offcoreToken = coreRefreshTokenFlow.getOffcoreToken();
        offcoreToken = coreRefreshTokenFlow.getOffcoreToken();
        Assertions.assertThat(offcoreToken).isEqualTo(mockOffcoreToken);
        Mockito.verify(tokenExchangeHelper, Mockito.times(1)).exchangeToken(Mockito.eq(mockToken));
    }

    @Test
    public void testOffcoreTokenWithRefreshTokenInvalidatedTest() throws TokenException {
        Properties properties = new Properties();
        properties.put("loginURL", "https://login.salesforce.com");
        properties.put("clientId", "somerandomclientid");
        properties.put("clientSecret", "clientSecret");
        properties.put("coreToken", "coreToken");
        properties.put("refreshToken", "refreshToken");

        RefreshTokenClient refreshTokenClient = Mockito.mock(RefreshTokenClient.class);
        CoreToken mockToken = new CoreToken();
        mockToken.setRefreshToken("some_refresh_token");
        mockToken.setAccessToken("some_access_token");
        Mockito.when(refreshTokenClient.getCoreToken(anyString(),anyString(),anyString(),anyString())).thenReturn(mockToken);
        TokenExchangeHelper tokenExchangeHelper = Mockito.mock(TokenExchangeHelper.class);
        OffcoreToken mockOffcoreToken = new OffcoreToken();
        mockOffcoreToken.setAccessToken("someaccesstoken");
        Calendar expiryTime = Calendar.getInstance();
        expiryTime.add(Calendar.HOUR, -100);
        mockOffcoreToken.setExpireTime(expiryTime);
        Mockito.when(tokenExchangeHelper.exchangeToken(Mockito.any())).thenReturn(mockOffcoreToken);
        RefreshTokenFlow coreRefreshTokenFlow = new RefreshTokenFlow(properties, refreshTokenClient, tokenExchangeHelper);
        OffcoreToken offcoreToken = coreRefreshTokenFlow.getOffcoreToken();
        offcoreToken = coreRefreshTokenFlow.getOffcoreToken();
        Assertions.assertThat(offcoreToken).isEqualTo(mockOffcoreToken);
        Mockito.verify(tokenExchangeHelper, Mockito.times(2)).exchangeToken(Mockito.eq(mockToken));
    }
}