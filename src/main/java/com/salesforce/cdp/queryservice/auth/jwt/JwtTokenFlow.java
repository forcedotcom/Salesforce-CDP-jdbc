package com.salesforce.cdp.queryservice.auth.jwt;

import com.salesforce.cdp.queryservice.auth.*;
import com.salesforce.cdp.queryservice.model.CoreTokenRenewResponse;
import com.salesforce.cdp.queryservice.util.Constants;
import com.salesforce.cdp.queryservice.util.TokenException;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

import static com.salesforce.cdp.queryservice.util.Messages.TOKEN_FETCH_FAILURE;

@Slf4j
public class JwtTokenFlow implements TokenProvider {

    private final Properties properties;
    private final JwtLoginClient client;
    private final TokenExchangeHelper tokenExchangeHelper;

    private OffcoreToken offcoreToken;

    public JwtTokenFlow(Properties properties, JwtLoginClient client, TokenExchangeHelper tokenExchangeHelper) {
        this.properties = properties;
        this.client = client;
        this.tokenExchangeHelper = tokenExchangeHelper;
    }

    @Override
    public CoreToken getCoreToken() throws TokenException {
        validateProperties(properties);
        String token_url = properties.getProperty(Constants.LOGIN_URL) + Constants.CORE_TOKEN_URL;
        try {
            String audience = getAudienceForJWTAssertion(properties.getProperty(Constants.LOGIN_URL));

            // And handle the initial login *without* immutables. This ensures that nothing
            // is allocated in memory that cannot be cleared on demand, and thus we aren't
            // at the garbage collectors mercy.
            return client.keyPairAuthLogin(Constants.TOKEN_GRANT_TYPE_JWT_BEARER,
                    properties.getProperty(Constants.CLIENT_ID),
                    properties.getProperty(Constants.USER_NAME),
                    properties.getProperty(Constants.PRIVATE_KEY),
                    audience, token_url);

        } catch (SQLException sqlException) {
            log.error("Caught exception while setting audience for JWT assertion", sqlException);
            throw new TokenException(TOKEN_FETCH_FAILURE, sqlException);
        } catch (Exception e) {
            log.error("Caught exception while retrieving the token", e);
            throw new TokenException(TOKEN_FETCH_FAILURE, e);
        }
    }

    @Override
    public OffcoreToken getOffcoreToken() throws TokenException {
        if (TokenUtils.isValid(offcoreToken)) return offcoreToken;
        CoreToken coreToken = getCoreToken();
        offcoreToken = tokenExchangeHelper.exchangeToken(coreToken);
        return offcoreToken;
    }

    private static void validateProperties(Properties properties) throws TokenException {
        if (properties.getProperty(Constants.USER_NAME) == null || properties.getProperty(Constants.USER_NAME).isEmpty()) {
            throw new TokenException("Username cannot be null/empty for key-pair authentication");
        }

        if (properties.getProperty(Constants.PRIVATE_KEY) == null || properties.getProperty(Constants.PRIVATE_KEY).isEmpty()) {
            throw new TokenException("Private key cannot be null/empty for key-pair authentication");
        }

        if (properties.getProperty(Constants.CLIENT_ID) == null || properties.getProperty(Constants.CLIENT_ID).isEmpty()) {
            throw new TokenException("Client Id cannot be null/empty for key-pair authentication");
        }
    }

    private static String getAudienceForJWTAssertion(String serviceRootUrl) throws SQLException {
        String serverUrl = serviceRootUrl.toLowerCase();
        if (serverUrl.contains(Constants.TEST_SERVER_URL)) {
            return Constants.DEV_TEST_SERVER_AUD;
        } else if (serverUrl.endsWith(Constants.PROD_SERVER_URL)) {
            return Constants.PROD_SERVER_AUD;
        } else {
            throw new SQLException("specified url: " + serviceRootUrl + " didn't match any existing envs");
        }
    }
}
