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

import java.io.UnsupportedEncodingException;
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

    /**
     * Get safe byte array from byte array
     * @param byteArray byteArray
     * @return byte array
     */
    public static byte[] safeByteArrayUrlEncode(byte[] byteArray) {
        int bytesNeedingReEncode = 0;

        // As per the URLEncode.encode() documentation we implement the byte array version here
        // (to keep us from forming immutable strings)
        // The alphanumeric characters "a" through "z", "A" through "Z" and "0" through "9" remain the same.
        // The special characters ".", "-", "*", and "_" remain the same.
        // The space character " " is converted into a plus sign "+".
        // All other characters are unsafe and are first converted into one or more bytes using some encoding scheme. Then each byte is represented by the 3-character string "%xy", where xy is the two-digit hexadecimal representation of the byte. The recommended encoding scheme to use is UTF-8. However, for compatibility reasons, if an encoding is not specified, then the default encoding of the platform is used.


        // First calculate all the bytes needing re-encode (these will require 3 characters per instead of one in the
        // new array)
        for (int i = 0; i < byteArray.length; i++) {
            if (!Character.isAlphabetic(byteArray[i]) &&
                    !Character.isDigit(byteArray[i]) &&
                    byteArray[i] != '.' &&
                    byteArray[i] != '-' &&
                    byteArray[i] != '*' &&
                    byteArray[i] != '_' &&
                    byteArray[i] != ' ') bytesNeedingReEncode++;
        }


        // Now allocate the new byte array to accomodate for the values, each encoded byte is 3 bytes now, but we already
        // have one byte of the three for the original bytes, so we only need to allocate origlength + bytesNeedingReEncode * 2
        byte [] encoded = new byte[byteArray.length + (bytesNeedingReEncode * 2)];

        int j = 0;
        for (int i = 0; i < byteArray.length; i++)
        {
            if (Character.isAlphabetic(byteArray[i]) ||
                    Character.isDigit(byteArray[i]) ||
                    byteArray[i] == '.' ||
                    byteArray[i] == '-' ||
                    byteArray[i] == '*' ||
                    byteArray[i] == '_') {

                encoded[j++] = byteArray[i];
            } else if (byteArray[i] == ' ') {
                encoded[j++] = '+';
            } else {
                encoded[j++] = '%';
                encoded[j++] = toHexDigit((byte)((byteArray[i] & 0xf0) >> 4));
                encoded[j++] = toHexDigit((byte)(byteArray[i] & 0x0f));
            }

            byteArray[i] = 0;
        }

        return encoded;
    }

    private static byte toHexDigit(byte b) {
        if (b > 9) {
            return (byte)('a' + (b - 10));
        } else {
            return (byte)('0' + b);
        }
    }

    public static byte[] asByteArray(Object property) throws UnsupportedEncodingException, IllegalArgumentException {
        if (property instanceof byte[])
        {
            return (byte[])property;
        }
        else if (property instanceof String)
        {
            return ((String) property).getBytes("utf-8");
        }

        throw new IllegalArgumentException(property.getClass().getName() + " is not a valid type for asByteArray()");
    }

}
