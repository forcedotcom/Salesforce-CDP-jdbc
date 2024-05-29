package com.salesforce.cdp.queryservice.auth;

import com.salesforce.cdp.queryservice.auth.token.CoreToken;
import com.salesforce.cdp.queryservice.auth.token.OffcoreToken;
import com.salesforce.cdp.queryservice.util.TokenException;
import okhttp3.OkHttpClient;

import java.util.Properties;

public class TokenManager {

    private final TokenProvider tokenProvider;

    public TokenManager(Properties properties, OkHttpClient client) throws TokenException {
        tokenProvider = TokenProviderFactory.getTokenProvider(properties, client);
    }

    public TokenManager(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    public CoreToken getCoreToken() throws TokenException {
        return tokenProvider.getCoreToken();
    }

    public OffcoreToken getOffcoreToken() throws TokenException {
        return tokenProvider.getOffcoreToken();
    }
}
