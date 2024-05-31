package com.salesforce.cdp.queryservice.auth.token;

import lombok.Data;
import lombok.ToString;

@Data
public class Token {

    @ToString.Exclude
    private String accessToken;

    private String instanceUrl;

    private String tokenType;
}
