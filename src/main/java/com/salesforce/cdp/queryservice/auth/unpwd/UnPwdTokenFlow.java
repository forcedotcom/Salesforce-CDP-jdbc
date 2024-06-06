package com.salesforce.cdp.queryservice.auth.unpwd;

import com.salesforce.cdp.queryservice.auth.*;
import com.salesforce.cdp.queryservice.auth.token.CoreToken;
import com.salesforce.cdp.queryservice.auth.token.OffcoreToken;
import com.salesforce.cdp.queryservice.util.Constants;
import com.salesforce.cdp.queryservice.util.TokenException;
import com.salesforce.cdp.queryservice.util.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

import static com.salesforce.cdp.queryservice.auth.TokenUtils.fillArray;
import static com.salesforce.cdp.queryservice.util.Messages.TOKEN_FETCH_FAILURE;

@Slf4j
public class UnPwdTokenFlow implements TokenProvider {

    private final Properties properties;
    private final UnPwdAuthClient client;
    private final TokenExchangeHelper tokenExchangeHelper;
    private OffcoreToken offcoreToken;

    public UnPwdTokenFlow(Properties properties, UnPwdAuthClient client, TokenExchangeHelper tokenExchangeHelper) {
        this.properties = properties;
        this.client = client;
        this.tokenExchangeHelper = tokenExchangeHelper;
    }

    @Override
    public CoreToken getCoreToken() throws TokenException {
        log.info("Getting core token using username password flow");
        validateProperties();
        String token_url = properties.getProperty(Constants.LOGIN_URL) + Constants.CORE_TOKEN_URL;
        log.info("Fetching core token from url: {}", token_url);
        byte[] passwordBytes = null;
        byte[] clientSecret = null;
        // Convert the password and client secret to byte arrays so, we can empty them at will.
        try {
            passwordBytes = Utils.safeByteArrayUrlEncode(Utils.asByteArray(properties.getProperty(Constants.PD)));
            clientSecret = Utils.asByteArray(properties.getProperty(Constants.CLIENT_SECRET));

            // And handle the initial login *without* immutables. This ensures that nothing
            // is allocated in memory that cannot be cleared on demand, and thus we aren't
            // at the garbage collectors mercy.
            return client.un_pw_login(Constants.TOKEN_GRANT_TYPE_PD,
                    properties.getProperty(Constants.CLIENT_ID),
                    clientSecret,
                    properties.getProperty(Constants.USER_NAME),
                    passwordBytes,
                    token_url);

        } catch (IOException e) {
            log.error("Caught exception while retrieving the token", e);
            throw new TokenException(TOKEN_FETCH_FAILURE, e);
        } finally {
            fillArray(passwordBytes, (byte) 0);
            fillArray(clientSecret, (byte)0);
        }
    }

    @Override
    public OffcoreToken getOffcoreToken() throws TokenException {
        if (TokenUtils.isValid(offcoreToken))
            return offcoreToken;
        CoreToken coreToken = getCoreToken();
        offcoreToken = tokenExchangeHelper.exchangeToken(coreToken);
        return offcoreToken;
    }

    private void validateProperties() throws TokenException {
        if (properties.getProperty(Constants.USER_NAME) == null) {
            throw new TokenException("Username cannot be null");
        }

        if (properties.getProperty(Constants.CLIENT_ID) == null) {
            throw new TokenException("Client Id cannot be null");
        }

        if (properties.getProperty(Constants.PD) == null) {
            throw new TokenException("Password cannot be null");
        }

        if (properties.getProperty(Constants.CLIENT_SECRET) == null) {
            throw new TokenException("Client Secret cannot be null");
        }
    }

}
