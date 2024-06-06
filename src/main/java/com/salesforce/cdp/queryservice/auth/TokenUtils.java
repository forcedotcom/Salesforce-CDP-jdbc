package com.salesforce.cdp.queryservice.auth;

import com.salesforce.cdp.queryservice.auth.token.CoreToken;
import com.salesforce.cdp.queryservice.auth.token.OffcoreToken;
import com.salesforce.cdp.queryservice.auth.token.Token;
import com.salesforce.cdp.queryservice.util.Constants;
import com.salesforce.cdp.queryservice.util.HttpHelper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Slf4j
public class TokenUtils {

    public static boolean isValid(OffcoreToken offcoreToken) {
        if (offcoreToken != null) {
            Calendar now = Calendar.getInstance();
            if (now.compareTo(offcoreToken.getExpireTime()) < 1) {
                log.info("Offcore token is still valid");
                return true;
            } else {
                log.info("Current token is expired");
            }
        } else {
            log.info("No existing tokens available");
        }
        return false;
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
            log.info("Successfully invalidated the core token");
        } catch (Exception e) {
            log.error("Revoking the core token failed", e);
        }
    }

    public static Map<String, String> getTokenWithUrl(Token token) {
        Map<String, String> tokenWithUrlMap = new HashMap<>();
        tokenWithUrlMap.put(Constants.ACCESS_TOKEN, token.getTokenType() + StringUtils.SPACE + token.getAccessToken());
        tokenWithUrlMap.put(Constants.TENANT_URL, token.getInstanceUrl());
        return tokenWithUrlMap;
    }

    public static void fillArray(byte[] bytes, byte val) {
        if (bytes != null) {
            Arrays.fill(bytes, val);
        }
    }
}
