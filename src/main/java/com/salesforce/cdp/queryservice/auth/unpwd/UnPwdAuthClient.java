package com.salesforce.cdp.queryservice.auth.unpwd;

import com.google.common.primitives.Bytes;
import com.salesforce.cdp.queryservice.auth.token.CoreToken;
import com.salesforce.cdp.queryservice.util.Constants;
import com.salesforce.cdp.queryservice.util.HttpHelper;
import com.salesforce.cdp.queryservice.util.TokenException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import static com.salesforce.cdp.queryservice.auth.TokenUtils.fillArray;
import static com.salesforce.cdp.queryservice.util.Messages.FAILED_LOGIN;

@Slf4j
public class UnPwdAuthClient {

    private final OkHttpClient client;

    public UnPwdAuthClient(OkHttpClient client) {
        this.client = client;
    }

    public CoreToken un_pw_login(
            String grantType, String clientId, byte[] clientSecret,
            String userName, byte[] passwordBytes,
            String tokenUrl
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
            log.info("Fetching the core token using UN Password Flow");
            RequestBody requestBody = RequestBody.create(body, MediaType.parse(Constants.URL_ENCODED_CONTENT));
            Map<String, String> headers = Collections.singletonMap(Constants.CONTENT_TYPE, Constants.URL_ENCODED_CONTENT);
            Request request = HttpHelper.buildRequest(Constants.POST, tokenUrl, requestBody, headers);
            long l = System.currentTimeMillis();
            log.info("Starting request to get core token using username password flow");
            Response response = client.newCall(request).execute();
            log.info("Finished request to get core token using username password flow in {}ms", System.currentTimeMillis() - l);
            if (!response.isSuccessful()) {
                log.error("login with user credentials failed with status code {}", response.code());
                HttpHelper.handleErrorResponse(response, Constants.ERROR_DESCRIPTION);
            }
            return HttpHelper.handleSuccessResponse(response, CoreToken.class);
        } catch (IOException e) {
            log.error("login with user credentials failed", e);
            throw new TokenException(FAILED_LOGIN, e);
        } finally {
            fillArray(body, (byte)0);
        }
    }
}
