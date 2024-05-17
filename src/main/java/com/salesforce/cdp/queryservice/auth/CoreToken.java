package com.salesforce.cdp.queryservice.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.ToString;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoreToken {

    @ToString.Exclude
    private String accessToken;

    private String scope;

    private String instanceUrl;

    private String tokenType;

    private String issuedAt;

    private String refreshToken;
}
