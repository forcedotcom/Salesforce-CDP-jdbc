package com.salesforce.cdp.queryservice.auth;

import com.salesforce.cdp.queryservice.util.TokenException;

public interface OffcoreTokenProvider {
    OffcoreToken getOffcoreToken() throws TokenException;
}
