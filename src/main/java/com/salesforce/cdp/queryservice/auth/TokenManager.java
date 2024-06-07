package com.salesforce.cdp.queryservice.auth;

import com.salesforce.cdp.queryservice.auth.token.CoreToken;
import com.salesforce.cdp.queryservice.auth.token.OffcoreToken;
import com.salesforce.cdp.queryservice.util.TokenException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.util.Properties;

@Slf4j
public class TokenManager {

    private final TokenProvider tokenProvider;

    public TokenManager(Properties properties, OkHttpClient client) throws TokenException {
        tokenProvider = TokenProviderFactory.getTokenProvider(properties, client);
    }

    public TokenManager(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    public synchronized CoreToken getCoreToken() throws TokenException {
        log.info("Begin fetching core token");
        long startTime = System.currentTimeMillis();
        CoreToken coreToken = tokenProvider.getCoreToken();
        log.info("Fetched core token in {}ms", System.currentTimeMillis() - startTime);
        return coreToken;
    }

    public synchronized OffcoreToken getOffcoreToken() throws TokenException {
        log.info("Begin fetching offcore token");
        long startTime = System.currentTimeMillis();
        OffcoreToken offcoreToken = tokenProvider.getOffcoreToken();
        log.info("Fetched offcore core token in {}ms", System.currentTimeMillis() - startTime);
        return offcoreToken;
    }
}
