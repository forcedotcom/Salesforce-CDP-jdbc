package com.salesforce.cdp.queryservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Calendar;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Token {
    private String access_token;

    private String instance_url;

    private String token_type;

    private int expires_in;

    private String error_description;

    private Calendar expire_time;
}
