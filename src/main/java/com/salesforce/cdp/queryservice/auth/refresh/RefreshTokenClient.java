package com.salesforce.cdp.queryservice.auth.refresh;

import com.salesforce.cdp.queryservice.auth.token.CoreToken;
import com.salesforce.cdp.queryservice.util.Constants;
import com.salesforce.cdp.queryservice.util.HttpHelper;
import com.salesforce.cdp.queryservice.util.TokenException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.salesforce.cdp.queryservice.util.Messages.FAILED_LOGIN_2;
import static com.salesforce.cdp.queryservice.util.Messages.RENEW_TOKEN;

@Slf4j
public class RefreshTokenClient {

    private final OkHttpClient client;

    public RefreshTokenClient(OkHttpClient client) {
        this.client = client;
    }

    public CoreToken getCoreToken(String url, String refreshToken, String clientId, String secret) throws TokenException {
        String token_url = url + Constants.CORE_TOKEN_URL;
        log.info("Fetching Refresh token using URL : {}", token_url);
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put(Constants.GRANT_TYPE_NAME, Constants.REFRESH_TOKEN_GRANT_TYPE);
        requestBody.put(Constants.CLIENT_ID_NAME, clientId);
        requestBody.put(Constants.CLIENT_SECRET_NAME, secret);
        requestBody.put(Constants.REFRESH_TOKEN_GRANT_TYPE, refreshToken);
        try {
            Response response = login(requestBody, token_url);
            return HttpHelper.handleSuccessResponse(response, CoreToken.class);
        }
        catch (IOException e) {
            log.error("Caught exception while renewing the core token", e);
            throw new TokenException(RENEW_TOKEN, e);
        }
    }

    private Response login(Map<String, String> requestBody, String url) throws TokenException {
        FormBody.Builder formBody = new FormBody.Builder();
        requestBody.entrySet().stream().filter(e ->  e.getValue() != null).forEach(e -> formBody.addEncoded(e.getKey(), e.getValue()));
        Map<String, String> headers = Collections.singletonMap(Constants.CONTENT_TYPE, Constants.URL_ENCODED_CONTENT);
        log.info(requestBody.toString());
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
}
