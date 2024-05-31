package com.salesforce.cdp.queryservice.auth;

import com.salesforce.cdp.queryservice.auth.token.CoreToken;
import com.salesforce.cdp.queryservice.auth.token.OffcoreToken;
import com.salesforce.cdp.queryservice.util.TokenException;

public interface TokenProvider {
    CoreToken getCoreToken() throws TokenException;
    OffcoreToken getOffcoreToken() throws TokenException;
}
