package com.salesforce.cdp.queryservice.auth;

import com.salesforce.cdp.queryservice.util.Constants;
import com.salesforce.cdp.queryservice.util.HttpHelper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TokenUtils {

    public static boolean isValid(OffcoreToken coreToken) {
        Calendar now = Calendar.getInstance();
        return now.compareTo(coreToken.getExpireTime()) < 1;
    }

    public static void invalidateCoreToken(String url, String coreToken, OkHttpClient client) {
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

    public static Map<String, String> getTokenWithUrl(OffcoreToken token) {
        Map<String, String> tokenWithUrlMap = new HashMap<>();
        tokenWithUrlMap.put(Constants.ACCESS_TOKEN, token.getTokenType() + StringUtils.SPACE + token.getAccessToken());
        tokenWithUrlMap.put(Constants.TENANT_URL, token.getInstanceUrl());
        return tokenWithUrlMap;
    }

    public static Map<String, String> getTokenWithUrl(CoreToken token) {
        Map<String, String> tokenWithUrlMap = new HashMap<>();
        tokenWithUrlMap.put(Constants.ACCESS_TOKEN, token.getTokenType() + StringUtils.SPACE + token.getAccessToken());
        tokenWithUrlMap.put(Constants.TENANT_URL, token.getInstanceUrl());
        return tokenWithUrlMap;
    }
}
