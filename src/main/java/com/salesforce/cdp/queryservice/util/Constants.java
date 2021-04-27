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

public class Constants {

    public static final String DATASOURCE_TYPE = "jdbc:queryService-jdbc:";
    public static final String BASE_URL = "baseUrl";
    public static final String LOGIN_URL = "loginURL";
    public static final String CDP_URL = "/api/v1";
    public static final String ANSI_SQL_URL = "/query?";
    public static final String METADATA_URL = "/metadata";
    public static final String TOKEN_EXCHANGE_URL = "/services/a360/token";
    public static final String TOKEN_REVOKE_URL = "/services/oauth2/revoke";
    public static final String CORE_TOKEN_URL = "/services/oauth2/token";
    public static final String PROTOCOL = "https://";
    public static final String QUERY_SERVICE = "cdp-query-service";
    public static final String QUERY_SERVICE_VERSION = "1.0";
    public static final String DRIVER_NAME = "QueryService-jdbc";
    public static final String DRIVER_VERSION = "1.0";

    // Parameter constants
    public static final String LIMIT = "limit=";
    public static final String OFFSET = "offset=";
    public static final String ORDERBY = "orderby=";
    public static final String AND = "&";

    // HTTP Client Constants
    public static final String AUTHORIZATION = "Authorization";
    public static final String USER_AGENT = "User-Agent";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String USER_AGENT_VALUE = "cdp/jdbc";
    public static final String TABLEAU_USER_AGENT_VALUE = "Tableau/Audiences360";
    public static final String JSON_CONTENT = "application/json";
    public static final String URL_ENCODED_CONTENT = "application/x-www-form-urlencoded";

    // Property Constants
    public static final String CORETOKEN = "coreToken";
    public static final String REFRESHTOKEN = "refreshToken";
    public static final String CLIENT_ID = "clientId";
    public static final String CLIENT_SECRET = "clientSecret";
    public static final String USER_NAME = "userName";
    public static final String PD = "password";

    // Response Constants
    public static final String MESSAGE = "message";
    public static final String ERROR_DESCRIPTION = "error_description";

    // HTTP methods
    public static final String POST = "POST";
    public static final String GET = "GET";

    // Integer Constants
    public static final int REST_TIME_OUT = 180;
    public static final Integer MAX_LIMIT = 4999;

    // Token Constants
    public static final String GRANT_TYPE_NAME = "grant_type";
    public static final String GRANT_TYPE = "urn:salesforce:grant-type:external:cdp";
    public static final String REFRESH_TOKEN_GRANT_TYPE = "refresh_token";
    public static final String TOKEN_GRANT_TYPE_PD = "password";
    public static final String SUBJECT_TOKEN_TYPE_NAME = "subject_token_type";
    public static final String SUBJECT_TOKEN_TYPE = "urn:ietf:params:oauth:token-type:access_token";
    public static final String SUBJECT_TOKEN = "subject_token";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String TENANT_URL = "tenantUrl";
    public static final String CLIENT_ID_NAME = "client_id";
    public static final String CLIENT_SECRET_NAME = "client_secret";
    public static final String CLIENT_USER_NAME = "username";
    public static final String CLIENT_PD = "password";

    // Header Constants
    public static final String TRACE_ID = "x-trace-id";
}
