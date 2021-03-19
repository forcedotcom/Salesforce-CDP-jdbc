package com.salesforce.cdp.queryservice.util;

import com.salesforce.cdp.queryservice.interfaces.ExtendedHttpStatusCode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Utils {

    private static Set<Integer> retryStatusCodes = new HashSet<>(Arrays.asList(ExtendedHttpStatusCode.SC_TOO_MANY_REQUESTS,
            ExtendedHttpStatusCode.SC_MOVED_TEMPORARILY,
            ExtendedHttpStatusCode.SC_GATEWAY_TIMEOUT,
            ExtendedHttpStatusCode.SC_SERVICE_UNAVAILABLE));

    private Utils() {
        //NOOP
    }

    public static Set<Integer> getRetryStatusCodes() {
        return retryStatusCodes;
    }

}
