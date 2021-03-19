package com.salesforce.cdp.queryservice.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.salesforce.cdp.queryservice.model.Token;
import okhttp3.*;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static com.salesforce.cdp.queryservice.ResponseEnum.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("jdk.internal.reflect.*")
public class TokenHelperTest {

    private Cache<String, Token> tokenCache;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Mock
    private OkHttpClient client;

    @Mock
    private Call remoteCall;

    private Properties properties;

    @Before
    public void init() {
        tokenCache = CacheBuilder.newBuilder().expireAfterWrite(10000, TimeUnit.MILLISECONDS).maximumSize(1).build();
        properties = new Properties();
        properties.put(Constants.LOGIN_URL, "https://login.stmpa.stm.salesforce.com");
        properties.put(Constants.CORETOKEN, "1234");
        properties.put(Constants.REFRESHTOKEN, "7347");
        properties.put(Constants.CLIENT_ID, "73abjd47");
        properties.put(Constants.CLIENT_SECRET, "73a0384bjd47");
    }

    @Test
    public void testCreateTokenSuccess() throws IOException, SQLException {
        String jsonString = TOKEN_EXCHANGE.getResponse();
        Response response = new Response.Builder().code(HttpStatus.SC_OK).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Success").
                body(ResponseBody.create(jsonString, MediaType.parse("application/json"))).build();
        when(remoteCall.execute()).thenReturn(response);
        when(client.newCall(any())).thenReturn(remoteCall);
        Map<String, String> tokenWithUrlMap = TokenHelper.getTokenWithTenantUrl(properties, client);
        Assert.assertEquals(tokenWithUrlMap.get(Constants.ACCESS_TOKEN), "Bearer 1234");
        Assert.assertEquals(tokenWithUrlMap.get(Constants.TENANT_URL), "abcd");
    }

    @Test
    public void testAlreadyExistingToken() throws IOException, SQLException, NoSuchFieldException, IllegalAccessException {
        Token token = new Token();
        token.setAccess_token("1234");
        token.setInstance_url("abcd");
        token.setToken_type("Bearer");
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 10);
        token.setExpire_time(now);
        tokenCache.put("1234", token);
        Field tokenCacheField = TokenHelper.class.getDeclaredField("tokenCache");
        tokenCacheField.setAccessible(true);
        tokenCacheField.set(null, tokenCache);
        Map<String, String> tokenWithUrlMap = TokenHelper.getTokenWithTenantUrl(properties, client);
        Assert.assertEquals(tokenWithUrlMap.get(Constants.ACCESS_TOKEN), "Bearer 1234");
        Assert.assertEquals(tokenWithUrlMap.get(Constants.TENANT_URL), "abcd");
    }

    @Test
    public void testExpiredToken() throws IllegalAccessException, NoSuchFieldException, IOException, SQLException {
        Token token = new Token();
        token.setAccess_token("1234");
        token.setInstance_url("abcd");
        token.setToken_type("Bearer");
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, -10);
        token.setExpire_time(now);
        tokenCache.put("1234", token);
        Field tokenCacheField = TokenHelper.class.getDeclaredField("tokenCache");
        tokenCacheField.setAccessible(true);
        tokenCacheField.set(null, tokenCache);
        String jsonString = RENEWED_CORE_TOKEN.getResponse();
        Response renewCoreTokenResponse = new Response.Builder().code(HttpStatus.SC_OK).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Success").
                body(ResponseBody.create(jsonString, MediaType.parse("application/json"))).build();
        Response offCoreResponse = new Response.Builder().code(HttpStatus.SC_OK).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Success").
                body(ResponseBody.create(TOKEN_EXCHANGE.getResponse(), MediaType.parse("application/json"))).build();
        when(remoteCall.execute()).thenReturn(renewCoreTokenResponse).thenReturn(offCoreResponse);
        when(client.newCall(any())).thenReturn(remoteCall);
        Map<String, String> tokenWithUrlMap = TokenHelper.getTokenWithTenantUrl(properties, client);
        Assert.assertEquals(tokenWithUrlMap.get(Constants.ACCESS_TOKEN), "Bearer 1234");
        Assert.assertEquals(tokenWithUrlMap.get(Constants.TENANT_URL), "abcd");
    }

    @Test
    public void testTokenExchangeWithException() throws IOException, SQLException {
        String jsonString = OAUTH_TOKEN_ERROR.getResponse();
        Response response = new Response.Builder().code(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Internal Server Error").
                body(ResponseBody.create(jsonString, MediaType.parse("application/json"))).build();
        when(remoteCall.execute()).thenReturn(response);
        when(client.newCall(any())).thenReturn(remoteCall);
        exceptionRule.expect(SQLException.class);
        exceptionRule.expectMessage("expired authorization code");
        TokenHelper.getTokenWithTenantUrl(properties, client);
    }

    @Test
    public void testCacheAfterInvalidingEntry() throws NoSuchFieldException, IllegalAccessException, IOException, SQLException {
        tokenCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MILLISECONDS).maximumSize(10).build();
        Token token = new Token();
        token.setAccess_token("1234");
        token.setInstance_url("abcd");
        token.setToken_type("Bearer");
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 10);
        token.setExpire_time(now);
        tokenCache.put("1234", token);
        Field tokenCacheField = TokenHelper.class.getDeclaredField("tokenCache");
        tokenCacheField.setAccessible(true);
        tokenCacheField.set(null, tokenCache);
        String jsonString = TOKEN_EXCHANGE.getResponse();
        Response response = new Response.Builder().code(HttpStatus.SC_OK).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Success").
                body(ResponseBody.create(jsonString, MediaType.parse("application/json"))).build();
        when(remoteCall.execute()).thenReturn(response);
        when(client.newCall(any())).thenReturn(remoteCall);
        Map<String, String> tokenWithUrlMap = TokenHelper.getTokenWithTenantUrl(properties, client);
        Assert.assertEquals(tokenWithUrlMap.get(Constants.ACCESS_TOKEN), "Bearer 1234");
        Assert.assertEquals(tokenWithUrlMap.get(Constants.TENANT_URL), "abcd");
    }

    private Request buildRequest() {
        return new Request.Builder()
                .url("https://gs0.salesforce.com/services/a360/token")
                .method(Constants.POST, RequestBody.create("{test: test}", MediaType.parse("application/json")))
                .build();
    }
}
