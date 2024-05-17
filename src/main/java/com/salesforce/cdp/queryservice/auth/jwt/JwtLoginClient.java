package com.salesforce.cdp.queryservice.auth.jwt;

import com.google.common.primitives.Bytes;
import com.salesforce.cdp.queryservice.auth.CoreToken;
import com.salesforce.cdp.queryservice.util.Constants;
import com.salesforce.cdp.queryservice.util.HttpHelper;
import com.salesforce.cdp.queryservice.util.TokenException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.salesforce.cdp.queryservice.util.Messages.FAILED_LOGIN;
import static com.salesforce.cdp.queryservice.util.Messages.JWT_CREATION_FAILURE;

@Slf4j
public class JwtLoginClient {

    private final OkHttpClient client;


    public JwtLoginClient(OkHttpClient client) {
        this.client = client;
    }

    public CoreToken keyPairAuthLogin(
            String grantType, String clientId,
            String userName, String privateKey, String audience, String tokenUrl
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
            return HttpHelper.handleSuccessResponse(response, CoreToken.class, false);
        } catch (IOException e) {
            log.error("login with user credentials failed", e);
            throw new TokenException(FAILED_LOGIN, e);
        } finally {
            Arrays.fill(grantTypeSegment, (byte)0);
            Arrays.fill(jwsSegment, (byte)0);
            Arrays.fill(separator, (byte)0);
            Arrays.fill(body, (byte)0);
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
