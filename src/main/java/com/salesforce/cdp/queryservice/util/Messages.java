package com.salesforce.cdp.queryservice.util;

// todo: replace messages with error codes or include codes with msg
public class Messages {

    public static String TOKEN_EXCHANGE_FAILURE = "Token exchange failed. Please login again.";

    public static String QUERY_EXCEPTION = "Failed to get the response for the query. Please try again.";

    public static String METADATA_EXCEPTION = "Failed to get the metadata. Please try again.";

    public static String TOKEN_FETCH_FAILURE = "Retrieving Token failed. Please try again.";

    public static String JWT_CREATION_FAILURE = "JWT assertion creation failed. Please check Username, Client Id, Private key and try again.";

    public static String FAILED_LOGIN = "Failed to login. Please check credentials";

    public static String FAILED_LOGIN_2 = "Failed to login(2). Please check credentials";

    public static String RENEW_TOKEN = "Failed to Renew Token. Please retry";

}
