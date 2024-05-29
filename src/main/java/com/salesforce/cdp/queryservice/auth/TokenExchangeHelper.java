package com.salesforce.cdp.queryservice.auth;

import com.salesforce.cdp.queryservice.auth.token.CoreToken;
import com.salesforce.cdp.queryservice.auth.token.OffcoreToken;
import com.salesforce.cdp.queryservice.util.Constants;
import com.salesforce.cdp.queryservice.util.HttpHelper;
import com.salesforce.cdp.queryservice.util.TokenException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

import static com.salesforce.cdp.queryservice.util.Messages.FAILED_LOGIN_2;
import static com.salesforce.cdp.queryservice.util.Messages.TOKEN_EXCHANGE_FAILURE;

@Slf4j
public class TokenExchangeHelper {

    private final Properties properties;
    private final OkHttpClient client;

    public TokenExchangeHelper(Properties properties, OkHttpClient client) {
        this.properties = properties;
        this.client = client;
    }

    public OffcoreToken exchangeToken(CoreToken coreToken) throws TokenException {
        return exchangeToken(properties.getProperty(Constants.LOGIN_URL),
                coreToken.getAccessToken(), properties.getProperty(Constants.DATASPACE));
    }

    private OffcoreToken exchangeToken(String url, String coreToken, String dataspace) throws TokenException {
        String token_url = url + Constants.TOKEN_EXCHANGE_URL;
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put(Constants.GRANT_TYPE_NAME, Constants.GRANT_TYPE);
        requestBody.put(Constants.SUBJECT_TOKEN_TYPE_NAME, Constants.SUBJECT_TOKEN_TYPE);
        requestBody.put(Constants.SUBJECT_TOKEN, coreToken);
        if(StringUtils.isNotBlank(dataspace)) {requestBody.put(Constants.DATASPACE,dataspace);}
        Calendar expireTime = Calendar.getInstance();
        Response response = null;
        try {
            response = login(requestBody, token_url);
            OffcoreToken token = HttpHelper.handleSuccessResponse(response, OffcoreToken.class, false);
            if (token.getErrorDescription() != null) {
                log.error("Token exchange failed with error {}", token.getErrorDescription());
                TokenUtils.invalidateCoreToken(url, coreToken, client);
                String message = token.getErrorDescription();
                throw new TokenException(message);
            }
            expireTime.add(Calendar.SECOND, token.getExpiresIn());
            token.setExpireTime(expireTime);
            return token;
        } catch (IOException e) {
            log.error("Caught exception while exchanging the offcore token", e);
            TokenUtils.invalidateCoreToken(url, coreToken, client);
            throw new TokenException(TOKEN_EXCHANGE_FAILURE, e);
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
