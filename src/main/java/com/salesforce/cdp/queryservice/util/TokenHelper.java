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

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TokenHelper {

    private static Cache<String, Token> tokenCache = CacheBuilder.newBuilder().expireAfterWrite(7200000, TimeUnit.MILLISECONDS).maximumSize(100).build();

    private TokenHelper() {
        //NOOP
    }

    public static Map<String, String> getTokenWithTenantUrl(Properties properties, OkHttpClient client) throws SQLException, IOException {
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

    private static Map<String, String> retrieveTokenWithPasswordGrant(Properties properties, OkHttpClient client) throws IOException, SQLException {
        String token_url = properties.getProperty(Constants.LOGIN_URL) + Constants.CORE_TOKEN_URL;
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put(Constants.GRANT_TYPE_NAME, Constants.TOKEN_GRANT_TYPE_PD);
        requestBody.put(Constants.CLIENT_ID_NAME, properties.getProperty(Constants.CLIENT_ID));
        requestBody.put(Constants.CLIENT_SECRET_NAME, properties.getProperty(Constants.CLIENT_SECRET));
        requestBody.put(Constants.CLIENT_USER_NAME, properties.getProperty(Constants.USER_NAME));
        requestBody.put(Constants.CLIENT_PD, properties.getProperty(Constants.PD));
        try {
            Response response = login(requestBody, token_url, client);
            CoreTokenRenewResponse coreTokenRenewResponse = HttpHelper.handleSuccessResponse(response, CoreTokenRenewResponse.class, false);
            Token token = exchangeToken(properties.getProperty(Constants.LOGIN_URL), coreTokenRenewResponse.getAccess_token(), client);
            tokenCache.put(properties.getProperty(Constants.USER_NAME), token);
            return getTokenWithUrl(token);
        } catch (Exception e) {
            log.error("Caught exception while retrieving the token", e);
            throw new SQLException(TOKEN_EXCHANGE_FAILURE);
        }
    }

    private static Map<String, String> renewToken(String url, String refreshToken, String clientId, String secret, OkHttpClient client) throws SQLException {
        String token_url = url + Constants.CORE_TOKEN_URL;
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put(Constants.GRANT_TYPE_NAME, Constants.REFRESH_TOKEN_GRANT_TYPE);
        requestBody.put(Constants.CLIENT_ID_NAME, clientId);
        requestBody.put(Constants.CLIENT_SECRET_NAME, secret);
        requestBody.put(Constants.REFRESH_TOKEN_GRANT_TYPE, refreshToken);
        try {
            Response response = login(requestBody, token_url, client);
            CoreTokenRenewResponse coreTokenRenewResponse = HttpHelper.handleSuccessResponse(response, CoreTokenRenewResponse.class, false);
            log.info("Renewed core token {}", coreTokenRenewResponse);
            Token token = exchangeToken(url, coreTokenRenewResponse.getAccess_token(), client);
            tokenCache.put(coreTokenRenewResponse.getAccess_token(), token);
            return getTokenWithUrl(token);
        } catch (IOException e) {
            log.error("Caught exception while renewing the core token", e);
            throw new SQLException(e.getMessage());
        }
    }

    private static Token exchangeToken(String url, String coreToken, OkHttpClient client) throws IOException, SQLException {
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
                String message = token.getError_description();
                throw new SQLException(message);
            }
            expire_time.add(Calendar.SECOND, token.getExpires_in());
            token.setExpire_time(expire_time);
            return token;
        } catch (IOException e) {
            log.error("Caught exception while getting the offcore token", e);
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
}
