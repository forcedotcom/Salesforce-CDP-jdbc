package com.salesforce.cdp.queryservice.auth.refresh;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.salesforce.cdp.queryservice.auth.*;
import com.salesforce.cdp.queryservice.auth.token.CoreToken;
import com.salesforce.cdp.queryservice.auth.token.OffcoreToken;
import com.salesforce.cdp.queryservice.util.Constants;
import com.salesforce.cdp.queryservice.util.TokenException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RefreshTokenFlow implements TokenProvider {

    private final Properties properties;
    private final RefreshTokenClient client;
    private final TokenExchangeHelper tokenExchangeHelper;

    private static final Cache<String, OffcoreToken> tokenCache = CacheBuilder.newBuilder()
            .expireAfterWrite(7200000, TimeUnit.MILLISECONDS)
            .maximumSize(100).build();

    public RefreshTokenFlow(Properties properties, RefreshTokenClient client, TokenExchangeHelper tokenExchangeHelper) {
        this.properties = properties;
        this.client = client;
        this.tokenExchangeHelper = tokenExchangeHelper;
    }

    @Override
    public CoreToken getCoreToken() throws TokenException {
        validateProperties();
        CoreToken coreToken = client.getCoreToken(properties.getProperty(Constants.LOGIN_URL),
                properties.getProperty(Constants.REFRESHTOKEN),
                properties.getProperty(Constants.CLIENT_ID),
                properties.getProperty(Constants.CLIENT_SECRET));
        updateRefreshTokenIfRequired(coreToken, properties);
        return coreToken;
    }

    @Override
    public OffcoreToken getOffcoreToken() throws TokenException {
        String coreTokenString = properties.getProperty(Constants.CORETOKEN);
        if(StringUtils.isNotBlank(coreTokenString)) {
            OffcoreToken offcoreToken = tokenCache.getIfPresent(coreTokenString);
            if (offcoreToken != null && TokenUtils.isValid(offcoreToken)) {
                return offcoreToken;
            }
        }
        CoreToken coreToken = getCoreToken();
        OffcoreToken offcoreToken = tokenExchangeHelper.exchangeToken(coreToken);
        if (StringUtils.isNotBlank(coreTokenString)) {
            tokenCache.put(coreTokenString, offcoreToken);
        }
        return offcoreToken;
    }

    private void updateRefreshTokenIfRequired(CoreToken coreToken, Properties properties) {
        if (StringUtils.isNotBlank(coreToken.getRefreshToken())) {
            properties.put(Constants.REFRESHTOKEN, coreToken.getRefreshToken());
        }
    }

    public void validateProperties() throws TokenException {
        if (properties.getProperty(Constants.CLIENT_ID) == null || properties.getProperty(Constants.CLIENT_ID).isEmpty()) {
            throw new TokenException("Client Id cannot be null/empty");
        }

        if (properties.getProperty(Constants.CLIENT_SECRET) == null || properties.getProperty(Constants.CLIENT_SECRET).isEmpty()) {
            throw new TokenException("Client Secret cannot be null/empty");
        }

        if (properties.getProperty(Constants.REFRESHTOKEN) == null || properties.getProperty(Constants.REFRESHTOKEN).isEmpty()) {
            throw new TokenException("Refresh Token cannot be null/empty");
        }

    }

}
