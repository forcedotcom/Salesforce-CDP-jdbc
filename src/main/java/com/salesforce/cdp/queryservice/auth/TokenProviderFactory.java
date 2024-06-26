package com.salesforce.cdp.queryservice.auth;

import com.salesforce.cdp.queryservice.auth.jwt.JwtTokenFlow;
import com.salesforce.cdp.queryservice.auth.jwt.JwtLoginClient;
import com.salesforce.cdp.queryservice.auth.refresh.RefreshTokenFlow;
import com.salesforce.cdp.queryservice.auth.refresh.RefreshTokenClient;
import com.salesforce.cdp.queryservice.auth.unpwd.UnPwdAuthClient;
import com.salesforce.cdp.queryservice.auth.unpwd.UnPwdTokenFlow;
import com.salesforce.cdp.queryservice.util.Constants;
import com.salesforce.cdp.queryservice.util.PropertyUtils;
import com.salesforce.cdp.queryservice.util.TokenException;
import okhttp3.OkHttpClient;

import java.util.Properties;

public class TokenProviderFactory {

    public static TokenProvider getTokenProvider(Properties properties, OkHttpClient client) throws TokenException {
        TokenExchangeHelper tokenExchangeHelper = new TokenExchangeHelper(properties, client);
        if (canSupportUsernamePasswordProvider(properties)) {
            return new UnPwdTokenFlow(properties, new UnPwdAuthClient(client), tokenExchangeHelper);
        } else if (canSupportJwtProvider(properties)) {
            return new JwtTokenFlow(properties, new JwtLoginClient(client), tokenExchangeHelper);
        } else if (canSupportRefreshToken(properties)) {
            return new RefreshTokenFlow(properties, new RefreshTokenClient(client),
                    tokenExchangeHelper);
        }
        throw new TokenException("Sufficient properties for deciding auth flow is not provided");
    }

    private static boolean canSupportUsernamePasswordProvider(Properties properties) {
        return PropertyUtils.isPropertyNonEmpty(properties, Constants.USER_NAME)
                && PropertyUtils.isPropertyNonEmpty(properties, Constants.PD);
    }

    private static boolean canSupportJwtProvider(Properties properties) {
        return PropertyUtils.isPropertyNonEmpty(properties, Constants.USER_NAME)
                && PropertyUtils.isPropertyNonEmpty(properties, Constants.PRIVATE_KEY);
    }

    private static boolean canSupportRefreshToken(Properties properties) {
        return PropertyUtils.isPropertyNonEmpty(properties, Constants.REFRESHTOKEN);
    }
}
