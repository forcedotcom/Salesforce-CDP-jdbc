package com.salesforce.cdp.queryservice.auth;

import com.salesforce.cdp.queryservice.util.TokenException;
import okhttp3.OkHttpClient;

import java.util.Properties;

public class TokenManager {

    private final TokenProvider tokenProvider;
    private final TokenExchangeHelper tokenExchangeHelper;

    private OffcoreToken offcoreToken = null;

    public TokenManager(Properties properties, OkHttpClient client) throws TokenException {
        tokenProvider = CoreTokenProviderFactory.getCoreTokenProvider(properties, client);
        tokenExchangeHelper = new TokenExchangeHelper(properties, client);
    }

    public TokenManager(TokenProvider tokenProvider, TokenExchangeHelper tokenExchangeHelper) {
        this.tokenProvider = tokenProvider;
        this.tokenExchangeHelper = tokenExchangeHelper;
    }

    public CoreToken getCoreToken() throws TokenException {
        return tokenProvider.getCoreToken();
    }

    public OffcoreToken getOffcoreToken() throws TokenException {
        if (tokenProvider instanceof OffcoreTokenProvider) {
            return ((OffcoreTokenProvider) tokenProvider).getOffcoreToken();
        }
        if (offcoreToken != null && TokenUtils.isValid(offcoreToken)) {
            return offcoreToken;
        }
        CoreToken coreToken = getCoreToken();
        OffcoreToken offcoreToken = tokenExchangeHelper.exchangeToken(coreToken);
        this.offcoreToken = offcoreToken;
        return offcoreToken;
    }
}
