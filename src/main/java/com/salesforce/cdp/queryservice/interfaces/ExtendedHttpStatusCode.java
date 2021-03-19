package com.salesforce.cdp.queryservice.interfaces;

import org.apache.http.HttpStatus;

public interface ExtendedHttpStatusCode extends HttpStatus {

    int SC_TOO_MANY_REQUESTS = 429;

}
