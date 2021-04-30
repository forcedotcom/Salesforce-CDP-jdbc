/*
 * Copyright (c) 2021, salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    /**
     * SQL contains % and _ wildCards.
     * This method converts these wildcards to regex.
     *
     * @param tableNamePattern tableNamePattern
     * @return regex
     */
    public static String convertPatternToRegEx(String tableNamePattern) {
        StringBuilder regEx = new StringBuilder();
        for (int i = 0; i < tableNamePattern.length(); i++) {
            if (tableNamePattern.charAt(i) == Constants.MULTIPLE_CHAR_MATCH) {
                // Match one or more chars
                regEx.append(".*");
            } else if (tableNamePattern.charAt(i) == Constants.SINGLE_CHAR_MATCH) {
                // Match any char
                regEx.append(".");
            } else {
                regEx.append(tableNamePattern.charAt(i));
            }
        }
        return regEx.toString();
    }
}
