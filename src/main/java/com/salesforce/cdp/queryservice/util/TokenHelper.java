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
import com.salesforce.cdp.queryservice.model.CoreTokenRenewResponse;
import com.salesforce.cdp.queryservice.model.Token;
import static com.salesforce.cdp.queryservice.util.Messages.TOKEN_EXCHANGE_FAILURE;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TokenHelper {

    private static Cache<String, Token> tokenCache = CacheBuilder.newBuilder().expireAfterWrite(7200000, TimeUnit.MILLISECONDS).maximumSize(100).build();

    private TokenHelper() {
        //NOOP
    }

    public static Map<String, String> getTokenWithTenantUrl(Properties properties, OkHttpClient client) throws SQLException {
        Token token = null;
        if(properties.containsKey(Constants.CORETOKEN)) {
            token = tokenCache.getIfPresent(properties.getProperty(Constants.CORETOKEN));
        } else if (properties.containsKey(Constants.USER_NAME)) {
            token = tokenCache.getIfPresent(properties.getProperty(Constants.USER_NAME));
        }
        if (token == null) {
            if(properties.containsKey(Constants.USER_NAME) && properties.containsKey(Constants.PD)) {
                return retrieveTokenWithPasswordGrant(properties, client);
            }
            Token newToken = exchangeToken(properties.getProperty(Constants.LOGIN_URL), properties.getProperty(Constants.CORETOKEN), client);
            tokenCache.put(properties.getProperty(Constants.CORETOKEN), newToken);
            return getTokenWithUrl(newToken);
        }
        if (token != null && isAlive(token)) {
            return getTokenWithUrl(token);
        } else {
            log.info("Renewing the token as the off-core token expired");
            if (properties.containsKey(Constants.CORETOKEN)) {
                clearToken(properties.getProperty(Constants.CORETOKEN));
            } else if (properties.containsKey(Constants.USER_NAME)) {
                clearToken(properties.getProperty(Constants.USER_NAME));
            }
            if(properties.containsKey(Constants.USER_NAME) && properties.containsKey(Constants.PD)) {
                return retrieveTokenWithPasswordGrant(properties, client);
            }
            return renewToken(properties.getProperty(Constants.LOGIN_URL), properties.getProperty(Constants.REFRESHTOKEN),
                    properties.getProperty(Constants.CLIENT_ID), properties.getProperty(Constants.CLIENT_SECRET), client);
        }
    }

    private static Map<String, String> retrieveTokenWithPasswordGrant(Properties properties, OkHttpClient client) throws SQLException {
        // Check for password and client secret.
        if (properties.getProperty(Constants.PD) == null) {
            throw new SQLException("Password cannot be null");
        }

        if (properties.getProperty(Constants.CLIENT_SECRET) == null) {
            throw new SQLException("Client Secret cannot be null");
        }

        String token_url = properties.getProperty(Constants.LOGIN_URL) + Constants.CORE_TOKEN_URL;
        CoreTokenRenewResponse coreTokenRenewResponse = null;
        byte[] passwordBytes = null;
        byte[] clientSecret = null;


        // Convert the password and client secret to byte arrays so we can empty them at will.
        try {
            passwordBytes = Utils.safeByteArrayUrlEncode(Utils.asByteArray(properties.getProperty(Constants.PD)));
            clientSecret = Utils.asByteArray(properties.getProperty(Constants.CLIENT_SECRET));


            // Remove the properties from the propertybag so that GC can collect them ASAP.
            properties.remove(Constants.PD);
            properties.remove(Constants.CLIENT_SECRET);


            // And handle the initial login *without* immutables. This ensures that nothing
            // is allocated in memory that cannot be cleared on demand and thus we aren't
            // at the garbage collectors mercy.
            String response = un_pw_login(Constants.TOKEN_GRANT_TYPE_PD,
                                          properties.getProperty(Constants.CLIENT_ID),
                                          clientSecret,
                                          properties.getProperty(Constants.USER_NAME),
                                          passwordBytes,
                                          token_url);


            // Then get rid of the secrets from memory
            Arrays.fill(passwordBytes, (byte)0);
            Arrays.fill(clientSecret, (byte)0);


            // And exchange the UN/PW flow authtoken for a scoped bearer token.
            coreTokenRenewResponse = HttpHelper.handleSuccessResponse(response, CoreTokenRenewResponse.class);
            Token token = exchangeToken(properties.getProperty(Constants.LOGIN_URL), coreTokenRenewResponse.getAccess_token(), client);
            tokenCache.put(properties.getProperty(Constants.USER_NAME), token);
            return getTokenWithUrl(token);
        } catch (Exception e) {
            log.error("Caught exception while retrieving the token", e);
            invalidateCoreToken(properties.getProperty(Constants.LOGIN_URL), coreTokenRenewResponse == null ? null : coreTokenRenewResponse.getAccess_token(), client);
            throw new SQLException(TOKEN_EXCHANGE_FAILURE);
        } finally {
            Arrays.fill(passwordBytes, (byte)0);
            Arrays.fill(clientSecret, (byte)0);
        }
    }

    private static Map<String, String> renewToken(String url, String refreshToken, String clientId, String secret, OkHttpClient client) throws SQLException {
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
            return getTokenWithUrl(token);
        } catch (Exception e) {
            log.error("Caught exception while renewing the core token", e);
            invalidateCoreToken(url, coreTokenRenewResponse == null ? null : coreTokenRenewResponse.getAccess_token(), client);
            throw new SQLException(e.getMessage());
        }
    }

    private static Token exchangeToken(String url, String coreToken, OkHttpClient client) throws SQLException {
        String token_url = url + Constants.TOKEN_EXCHANGE_URL;
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put(Constants.GRANT_TYPE_NAME, Constants.GRANT_TYPE);
        requestBody.put(Constants.SUBJECT_TOKEN_TYPE_NAME, Constants.SUBJECT_TOKEN_TYPE);
        requestBody.put(Constants.SUBJECT_TOKEN, coreToken);
        Calendar expire_time = Calendar.getInstance();
        try {
            Response response = login(requestBody, token_url, client);
            Token token = HttpHelper.handleSuccessResponse(response, Token.class, false);
            if (token.getError_description() != null) {
                log.error("Token exchange failed with error {}", token.getError_description());
                invalidateCoreToken(url, coreToken, client);
                String message = token.getError_description();
                throw new SQLException(message);
            }
            expire_time.add(Calendar.SECOND, token.getExpires_in());
            token.setExpire_time(expire_time);
            return token;
        } catch (Exception e) {
            log.error("Caught exception while getting the offcore token", e);
            invalidateCoreToken(url, coreToken, client);
            throw new SQLException(TOKEN_EXCHANGE_FAILURE);
        }
    }

    private static boolean isAlive(Token token) {
        Calendar now = Calendar.getInstance();
        return now.compareTo(token.getExpire_time()) < 1;
    }

    private static Map<String, String> getTokenWithUrl(Token token) {
        Map<String, String> tokenWithUrlMap = new HashMap<>();
        tokenWithUrlMap.put(Constants.ACCESS_TOKEN, token.getToken_type() + StringUtils.SPACE + token.getAccess_token());
        tokenWithUrlMap.put(Constants.TENANT_URL, token.getInstance_url());
        return tokenWithUrlMap;
    }

    private static void clearToken(String tokenKey) {
        tokenCache.invalidate(tokenKey);
    }

    private static String un_pw_login(String grantType, String clientId, byte[] clientSecret, String userName, byte[] passwordBytes, String tokenUrl) throws Exception
    {
        byte[] grantTypeSegment = ("grant_type=" + grantType).getBytes("utf-8");
        byte[] clientIdSegment = ("client_id=" + clientId).getBytes("utf-8");
        byte[] clientSecretSegment = "client_secret=".getBytes("utf-8");
        byte[] userNameSegment = ("username=" + URLEncoder.encode(userName)).getBytes("utf-8");
        byte[] passwordSegment = ("password=").getBytes("utf-8");
        byte[] separator = "&".getBytes("utf-8");


        // Pre-calculate the size of the postdata in bytes we'll be sending for Content-Length.
        int postDataLength = grantTypeSegment.length +
                             separator.length +
                             clientIdSegment.length +
                             separator.length +
                             clientSecretSegment.length +
                             clientSecret.length +
                             separator.length +
                             userNameSegment.length +
                             separator.length +
                             passwordSegment.length +
                             passwordBytes.length;


        // Setup the connection parameters and write out the POST body
        HttpURLConnection connection = (HttpURLConnection)(new URL(tokenUrl).openConnection());
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "cdp/jdbc");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setUseCaches(false);

        OutputStream os = connection.getOutputStream();
        os.write(grantTypeSegment);
        os.write(separator);
        os.write(clientIdSegment);
        os.write(separator);
        os.write(clientSecretSegment);
        os.write(clientSecret);
        os.write(separator);
        os.write(userNameSegment);
        os.write(separator);
        os.write(passwordSegment);
        os.write(passwordBytes);
        os.flush();


        // Read back the response body.
        BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            sb.append(output);
        }

        String message = sb.toString();

        // And return the message we got or error it.
        if (connection.getResponseCode() != 200) {
            log.error("Token exchange failed with status code {}", connection.getResponseCode());
            HttpHelper.handleErrorResponse(message, Constants.ERROR_DESCRIPTION);
        }


        return message;
    }


    private static Response login(Map<String, String> requestBody, String url, OkHttpClient client) throws IOException, SQLException {
        FormBody.Builder formBody = new FormBody.Builder();
        requestBody.entrySet().stream().forEach(e -> formBody.addEncoded(e.getKey(), e.getValue()));
        Map<String, String> headers = Collections.singletonMap(Constants.CONTENT_TYPE, Constants.URL_ENCODED_CONTENT);
        Request request = HttpHelper.buildRequest(Constants.POST, url, formBody.build(), headers);
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            log.error("Token exchange failed with status code {}", response.code());
            HttpHelper.handleErrorResponse(response, Constants.ERROR_DESCRIPTION);
        }
        return response;
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
}
