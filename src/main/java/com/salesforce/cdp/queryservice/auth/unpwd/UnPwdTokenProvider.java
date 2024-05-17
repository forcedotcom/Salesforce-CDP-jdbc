package com.salesforce.cdp.queryservice.auth.unpwd;

import com.salesforce.cdp.queryservice.auth.*;
import com.salesforce.cdp.queryservice.util.Constants;
import com.salesforce.cdp.queryservice.util.TokenException;
import com.salesforce.cdp.queryservice.util.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

import static com.salesforce.cdp.queryservice.util.Messages.TOKEN_FETCH_FAILURE;

@Slf4j
public class UnPwdTokenProvider implements CoreTokenProvider {

    private final Properties properties;
    private final UnPwdAuthClient client;

    public UnPwdTokenProvider(Properties properties, UnPwdAuthClient client) {
        this.properties = properties;
        this.client = client;
    }

    @Override
    public CoreToken getCoreToken() throws TokenException {
        validateProperties();
        String token_url = properties.getProperty(Constants.LOGIN_URL) + Constants.CORE_TOKEN_URL;
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
            Arrays.fill(passwordBytes, (byte) 0);
            Arrays.fill(clientSecret, (byte)0);
        }
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
