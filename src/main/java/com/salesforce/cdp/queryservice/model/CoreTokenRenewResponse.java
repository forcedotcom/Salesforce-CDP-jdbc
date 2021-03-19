package com.salesforce.cdp.queryservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoreTokenRenewResponse {
    private String access_token;

    private String scope;

    private String instance_url;

    private String token_type;

    private String issued_at;

    @Override
    public String toString() {
        return String.format("scope : %s instance_url : %s token_type : %s issued_at : %s", scope, instance_url, token_type, issued_at);
    }
}
