/*
 * Copyright (c) 2021, salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.salesforce.cdp.queryservice.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.primitives.Bytes;
import com.salesforce.cdp.queryservice.model.CoreTokenRenewResponse;
import com.salesforce.cdp.queryservice.model.Token;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.salesforce.cdp.queryservice.util.Messages.*;

@Slf4j
public class TokenHelper {

    private static Cache<String, Token> tokenCache = CacheBuilder.newBuilder()
            .expireAfterWrite(7200000, TimeUnit.MILLISECONDS)
            .maximumSize(100).build();

    private TokenHelper() {
        //NOOP
    }

    /**
     * Gets the token for specified credentials
     *
     * @param properties Contains the credentials
     * @param client okHttp client
     * @return token containing info that can be used for querying
     * @throws TokenException when unable to resolve or fetch token
     */
    public static Token getToken(Properties properties, OkHttpClient client) throws TokenException {
        Token token = null;
        if(properties.containsKey(Constants.CORETOKEN)) {
            token = tokenCache.getIfPresent(properties.getProperty(Constants.CORETOKEN));
        }
        if (token == null) {
            if(properties.containsKey(Constants.USER_NAME) && !properties.getProperty(Constants.USER_NAME).isEmpty()
                    && properties.containsKey(Constants.PD) && !properties.getProperty(Constants.PD).isEmpty()) {
                return retrieveTokenWithPasswordGrant(properties, client);
            } else if (properties.containsKey(Constants.USER_NAME) && !properties.getProperty(Constants.USER_NAME).isEmpty()
                    && properties.containsKey(Constants.PRIVATE_KEY)) {
                return retrieveTokenWithJWTBearerGrant(properties, client);
            }
            Token newToken = exchangeToken(properties.getProperty(Constants.LOGIN_URL), properties.getProperty(Constants.CORETOKEN), client);
            tokenCache.put(properties.getProperty(Constants.CORETOKEN), newToken);
            return newToken;
        }
        if (isAlive(token)) {
            return token;
        } else {
            log.info("Renewing the token as the off-core token expired");
            if (properties.containsKey(Constants.CORETOKEN)) {
                clearToken(properties.getProperty(Constants.CORETOKEN));
            }
            return renewToken(properties.getProperty(Constants.LOGIN_URL), properties.getProperty(Constants.REFRESHTOKEN),
                    properties.getProperty(Constants.CLIENT_ID), properties.getProperty(Constants.CLIENT_SECRET), client);
        }
    }

    private static Token retrieveTokenWithPasswordGrant(Properties properties, OkHttpClient client) throws TokenException {
        // Convert password to byte array as per SA
        if (properties.getProperty(Constants.PD) == null) {
            throw new TokenException("Password cannot be null");
        }

        if (properties.getProperty(Constants.CLIENT_SECRET) == null) {
            throw new TokenException("Client Secret cannot be null");
        }

        String token_url = properties.getProperty(Constants.LOGIN_URL) + Constants.CORE_TOKEN_URL;
        CoreTokenRenewResponse coreTokenRenewResponse = null;
        byte[] passwordBytes = null;
        byte[] clientSecret = null;
        // Convert the password and client secret to byte arrays so, we can empty them at will.
        try {
            passwordBytes = Utils.safeByteArrayUrlEncode(Utils.asByteArray(properties.getProperty(Constants.PD)));
            clientSecret = Utils.asByteArray(properties.getProperty(Constants.CLIENT_SECRET));

            // And handle the initial login *without* immutables. This ensures that nothing
            // is allocated in memory that cannot be cleared on demand, and thus we aren't
            // at the garbage collectors mercy.
            Response response = un_pw_login(Constants.TOKEN_GRANT_TYPE_PD,
                                          properties.getProperty(Constants.CLIENT_ID),
                                          clientSecret,
                                          properties.getProperty(Constants.USER_NAME),
                                          passwordBytes,
                                          token_url, client);

            // Then get rid of the secrets from memory
            // todo: since it is handled in finally, should we remove this?
            fillArray(passwordBytes, (byte) 0);
            fillArray(clientSecret, (byte)0);

            // And exchange the UN/PW flow authtoken for a scoped bearer token.
            coreTokenRenewResponse = HttpHelper.handleSuccessResponse(response, CoreTokenRenewResponse.class, false);
            return exchangeToken(coreTokenRenewResponse.getInstance_url(), coreTokenRenewResponse.getAccess_token(), client);
        } catch (IOException e) {
            log.error("Caught exception while retrieving the token", e);
            throw new TokenException(TOKEN_FETCH_FAILURE, e);
        } finally {
            fillArray(passwordBytes, (byte) 0);
            fillArray(clientSecret, (byte)0);
        }
    }

    private static Token retrieveTokenWithJWTBearerGrant(Properties properties, OkHttpClient client) throws TokenException {
        if (properties.getProperty(Constants.USER_NAME) == null || properties.getProperty(Constants.USER_NAME).isEmpty()) {
            throw new TokenException("Username cannot be null/empty for key-pair authentication");
        }

        if (properties.getProperty(Constants.PRIVATE_KEY) == null || properties.getProperty(Constants.PRIVATE_KEY).isEmpty()) {
            throw new TokenException("Private key cannot be null/empty for key-pair authentication");
        }

        if (properties.getProperty(Constants.CLIENT_ID) == null || properties.getProperty(Constants.CLIENT_ID).isEmpty()) {
            throw new TokenException("Client Id cannot be null/empty for key-pair authentication");
        }

        String token_url = properties.getProperty(Constants.LOGIN_URL) + Constants.CORE_TOKEN_URL;
        CoreTokenRenewResponse coreTokenRenewResponse = null;
        try {
            String audience = getAudienceForJWTAssertion(properties.getProperty(Constants.LOGIN_URL));

            // And handle the initial login *without* immutables. This ensures that nothing
            // is allocated in memory that cannot be cleared on demand, and thus we aren't
            // at the garbage collectors mercy.
            Response response = key_pair_auth_login(Constants.TOKEN_GRANT_TYPE_JWT_BEARER,
                    properties.getProperty(Constants.CLIENT_ID),
                    properties.getProperty(Constants.USER_NAME),
                    properties.getProperty(Constants.PRIVATE_KEY),
                    audience, token_url, client);

            // And exchange the Key/Pair flow authtoken for a scoped bearer token.
            coreTokenRenewResponse = HttpHelper.handleSuccessResponse(response, CoreTokenRenewResponse.class, false);
            return exchangeToken(coreTokenRenewResponse.getInstance_url(), coreTokenRenewResponse.getAccess_token(), client);
        } catch(SQLException sqlException) {
          log.error("Caught exception while setting audience for JWT assertion", sqlException);
          throw new TokenException(TOKEN_FETCH_FAILURE, sqlException);
        } catch (IOException e) {
            log.error("Caught exception while retrieving the token", e);
            throw new TokenException(TOKEN_FETCH_FAILURE, e);
        }
    }

    private static String getAudienceForJWTAssertion(String serviceRootUrl) throws SQLException {
        String serverUrl = serviceRootUrl.toLowerCase();
        if (serverUrl.endsWith(Constants.NA45_SERVER_URL) || serverUrl.endsWith(Constants.NA46_SERVER_URL)) {
            return Constants.DEV_TEST_SERVER_AUD;
        } else if (serverUrl.endsWith(Constants.PROD_SERVER_URL)) {
            return Constants.PROD_SERVER_AUD;
        } else {
            throw new SQLException("specified url: " + serviceRootUrl + " didn't match any existing envs");
        }
    }

    private static void fillArray(byte[] bytes, byte val) {
        if (bytes != null) {
            Arrays.fill(bytes, val);
        }
    }

    private static Token renewToken(String url, String refreshToken, String clientId, String secret, OkHttpClient client) throws TokenException {
        String token_url = url + Constants.CORE_TOKEN_URL;
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put(Constants.GRANT_TYPE_NAME, Constants.REFRESH_TOKEN_GRANT_TYPE);
        requestBody.put(Constants.CLIENT_ID_NAME, clientId);
        requestBody.put(Constants.CLIENT_SECRET_NAME, secret);
        requestBody.put(Constants.REFRESH_TOKEN_GRANT_TYPE, refreshToken);
        CoreTokenRenewResponse coreTokenRenewResponse = null;
        try {
            Response response = login(requestBody, token_url, client);
            coreTokenRenewResponse = HttpHelper.handleSuccessResponse(response, CoreTokenRenewResponse.class, false);
            log.info("Renewed core token {}", coreTokenRenewResponse);
            Token token = exchangeToken(url, coreTokenRenewResponse.getAccess_token(), client);
            tokenCache.put(coreTokenRenewResponse.getAccess_token(), token);
            return token;
        } catch (IOException e) {
            log.error("Caught exception while renewing the core token", e);
            throw new TokenException(RENEW_TOKEN, e);
        }
    }

    private static Token exchangeToken(String url, String coreToken, OkHttpClient client) throws TokenException {
        String token_url = url + Constants.TOKEN_EXCHANGE_URL;
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put(Constants.GRANT_TYPE_NAME, Constants.GRANT_TYPE);
        requestBody.put(Constants.SUBJECT_TOKEN_TYPE_NAME, Constants.SUBJECT_TOKEN_TYPE);
        requestBody.put(Constants.SUBJECT_TOKEN, coreToken);
        Calendar expire_time = Calendar.getInstance();
        Response response = null;
        try {
            response = login(requestBody, token_url, client);
            Token token = HttpHelper.handleSuccessResponse(response, Token.class, false);
            if (token.getError_description() != null) {
                log.error("Token exchange failed with error {}", token.getError_description());
                invalidateCoreToken(url, coreToken, client);
                String message = token.getError_description();
                throw new TokenException(message);
            }
            expire_time.add(Calendar.SECOND, token.getExpires_in());
            token.setExpire_time(expire_time);
            return token;
        } catch (IOException e) {
            log.error("Caught exception while exchanging the offcore token", e);
            invalidateCoreToken(url, coreToken, client);
            throw new TokenException(TOKEN_EXCHANGE_FAILURE, e);
        }
    }

    public static boolean isAlive(Token token) {
        Calendar now = Calendar.getInstance();
        return now.compareTo(token.getExpire_time()) < 1;
    }

    public static Map<String, String> getTokenWithUrl(Token token) {
        Map<String, String> tokenWithUrlMap = new HashMap<>();
        tokenWithUrlMap.put(Constants.ACCESS_TOKEN, token.getToken_type() + StringUtils.SPACE + token.getAccess_token());
        tokenWithUrlMap.put(Constants.TENANT_URL, token.getInstance_url());
        return tokenWithUrlMap;
    }

    public static boolean tokenExistsInCache(String coreToken) {
        return tokenCache.getIfPresent(coreToken) != null;
    }

    private static void clearToken(String tokenKey) {
        tokenCache.invalidate(tokenKey);
    }

    private static Response login(Map<String, String> requestBody, String url, OkHttpClient client) throws TokenException {
        FormBody.Builder formBody = new FormBody.Builder();
        requestBody.forEach(formBody::addEncoded);
        Map<String, String> headers = Collections.singletonMap(Constants.CONTENT_TYPE, Constants.URL_ENCODED_CONTENT);
        try {
            Request request = HttpHelper.buildRequest(Constants.POST, url, formBody.build(), headers);
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                log.error("login failed with status code {}", response.code());
                HttpHelper.handleErrorResponse(response, Constants.ERROR_DESCRIPTION);
            }
            return response;
        } catch (IOException e) {
            log.error("login failed", e);
            throw new TokenException(FAILED_LOGIN_2, e);
        }
    }

    private static void invalidateCoreToken(String url, String coreToken, OkHttpClient client) {
        if (coreToken == null) {
            return;
        }
        try {
            log.info("Invalidating the core token");
            FormBody formBody = new FormBody.Builder().addEncoded("token", coreToken).build();
            Map<String, String> headers = Collections.singletonMap(Constants.CONTENT_TYPE, Constants.URL_ENCODED_CONTENT);
            String tokenRevokeUrl = url + Constants.TOKEN_REVOKE_URL;
            Request request = HttpHelper.buildRequest(Constants.POST, tokenRevokeUrl, formBody, headers);
            // Response is not needed for this call.
            client.newCall(request).execute();
        } catch (Exception e) {
            log.error("Revoking the core token failed", e);
        }
    }

    private static Response un_pw_login(
            String grantType, String clientId, byte[] clientSecret,
            String userName, byte[] passwordBytes,
            String tokenUrl, OkHttpClient client
    ) throws TokenException {
        byte[] grantTypeSegment = (Constants.GRANT_TYPE_NAME + Constants.TOKEN_ASSIGNMENT + grantType)
                .getBytes(StandardCharsets.UTF_8);
        byte[] clientIdSegment = (Constants.CLIENT_ID_NAME + Constants.TOKEN_ASSIGNMENT + clientId)
                .getBytes(StandardCharsets.UTF_8);
        byte[] clientSecretSegment = (Constants.CLIENT_SECRET_NAME + Constants.TOKEN_ASSIGNMENT)
                .getBytes(StandardCharsets.UTF_8);
        byte[] userNameSegment = (Constants.CLIENT_USER_NAME + Constants.TOKEN_ASSIGNMENT +
                URLEncoder.encode(userName)).getBytes(StandardCharsets.UTF_8);
        byte[] passwordSegment = (Constants.CLIENT_PD + Constants.TOKEN_ASSIGNMENT).getBytes(StandardCharsets.UTF_8);
        byte[] separator = Constants.TOKEN_SEPARATOR.getBytes(StandardCharsets.UTF_8);

        byte[] body = Bytes.concat(
                grantTypeSegment, separator, clientIdSegment, separator, clientSecretSegment, clientSecret,
                separator, userNameSegment, separator, passwordSegment, passwordBytes
        );
        try {
            RequestBody requestBody = RequestBody.create(body, MediaType.parse(Constants.URL_ENCODED_CONTENT));
            Map<String, String> headers = Collections.singletonMap(Constants.CONTENT_TYPE, Constants.URL_ENCODED_CONTENT);
            Request request = HttpHelper.buildRequest(Constants.POST, tokenUrl, requestBody, headers);
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                log.error("login with user credentials failed with status code {}", response.code());
                HttpHelper.handleErrorResponse(response, Constants.ERROR_DESCRIPTION);
            }
            return response;
        } catch (IOException e) {
            log.error("login with user credentials failed", e);
            throw new TokenException(FAILED_LOGIN, e);
        } finally {
            fillArray(body, (byte)0);
        }
    }

    private static Response key_pair_auth_login(
            String grantType, String clientId,
            String userName, String privateKey, String audience, String tokenUrl, OkHttpClient client
    ) throws TokenException {
        byte[] grantTypeSegment = (Constants.GRANT_TYPE_NAME + Constants.TOKEN_ASSIGNMENT + grantType)
                .getBytes(StandardCharsets.UTF_8);
        byte[] jwsSegment = (Constants.ASSERTION + Constants.TOKEN_ASSIGNMENT +
                createJwt(clientId, userName, privateKey, audience)).getBytes(StandardCharsets.UTF_8);
        byte[] separator = Constants.TOKEN_SEPARATOR.getBytes(StandardCharsets.UTF_8);

        byte[] body = Bytes.concat(
                grantTypeSegment, separator, jwsSegment
        );
        try {
            RequestBody requestBody = RequestBody.create(body, MediaType.parse(Constants.URL_ENCODED_CONTENT));
            Map<String, String> headers = Collections.singletonMap(Constants.CONTENT_TYPE, Constants.URL_ENCODED_CONTENT);
            Request request = HttpHelper.buildRequest(Constants.POST, tokenUrl, requestBody, headers);
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                log.error("login with user credentials failed with status code {}", response.code());
                HttpHelper.handleErrorResponse(response, Constants.ERROR_DESCRIPTION);
            }
            return response;
        } catch (IOException e) {
            log.error("login with user credentials failed", e);
            throw new TokenException(FAILED_LOGIN, e);
        } finally {
            fillArray(grantTypeSegment, (byte)0);
            fillArray(jwsSegment, (byte)0);
            fillArray(separator, (byte)0);
            fillArray(body, (byte)0);
        }
    }

    private static String createJwt(String clientId, String userName, String privateKey, String audience) throws TokenException {
        Instant now = Instant.now();
        String jwtToken = null;
        try {
            RSAPrivateKey rsaPrivateKey = getPrivateKey(privateKey);
            jwtToken = Jwts.builder()
                    .setIssuer(clientId)
                    .setSubject(userName)
                    .setAudience(audience)
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(now.plus(2l, ChronoUnit.MINUTES)))
                    .signWith(rsaPrivateKey, SignatureAlgorithm.RS256)
                    .compact();
        } catch (Exception e) {
            log.error("JWT assertion creation failed", e);
            throw new TokenException(JWT_CREATION_FAILURE, e);
        }

        return jwtToken;
    }

    private static RSAPrivateKey getPrivateKey(String rsaPrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        rsaPrivateKey = rsaPrivateKey.replace(Constants.BEGIN_PRIVATE_KEY, "");
        rsaPrivateKey = rsaPrivateKey.replace(Constants.END_PRIVATE_KEY, "");
        rsaPrivateKey = rsaPrivateKey.replaceAll("\\s", "");

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(rsaPrivateKey));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateKey privateKey = (RSAPrivateKey)kf.generatePrivate(keySpec);
        return privateKey;
    }
}
