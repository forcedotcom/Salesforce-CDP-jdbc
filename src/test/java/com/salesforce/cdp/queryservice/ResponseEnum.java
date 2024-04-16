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

package com.salesforce.cdp.queryservice;

public enum ResponseEnum {

    INTERNAL_SERVER_ERROR("{\n" +
            "    \"timestamp\": \"2021-01-08T11:53:29.668+0000\",\n" +
            "    \"error\": \"Internal Server Error\",\n" +
            "    \"message\": \"Internal Server Error\",\n" +
            "    \"internalErrorCode\": \"COMMON_ERROR_GENERIC\",\n" +
            "    \"details\": {\n" +
            "        \"status\": \"Internal Server Error\",\n" +
            "        \"statusCode\": 500\n" +
            "    }\n" +
            "}"),

    TABLE_METADATA("{\n" +
            "    \"metadata\": [\n" +
            "        {\n" +
            "            \"fields\": [\n" +
            "                {\n" +
            "                    \"name\": \"DataSourceId__c\",\n" +
            "                    \"type\": \"STRING\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"name\": \"DataSourceObjectId__c\",\n" +
            "                    \"type\": \"STRING\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"name\": \"EmailAddress__c\",\n" +
            "                    \"type\": \"STRING\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"name\": \"Id__c\",\n" +
            "                    \"type\": \"STRING\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"name\": \"InternalOrganizationId__c\",\n" +
            "                    \"type\": \"STRING\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"name\": \"PartyId__c\",\n" +
            "                    \"type\": \"STRING\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"category\": \"Profile\",\n" +
            "            \"name\": \"ContactPointEmail__dlm\",\n" +
            "            \"relationships\": [\n" +
            "                {\n" +
            "                    \"fromEntity\": \"ContactPointEmail__dlm\",\n" +
            "                    \"toEntity\": \"ContactPointEmailIdentityLink__dlm\",\n" +
            "                    \"fromEntityAttribute\": \"Id__c\",\n" +
            "                    \"toEntityAttribute\": \"SourceRecordId__c\",\n" +
            "                    \"cardinality\": \"ONETOONE\"\n" +
            "                },\n" +
            "                {\n" +
            "                    \"fromEntity\": \"ContactPointEmail__dlm\",\n" +
            "                    \"toEntity\": \"Individual__dlm\",\n" +
            "                    \"fromEntityAttribute\": \"PartyId__c\",\n" +
            "                    \"toEntityAttribute\": \"Id__c\",\n" +
            "                    \"cardinality\": \"NTOONE\"\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    ]\n" +
            "}"),
    NOT_FOUND("{\n" +
            "    \"timestamp\": \"2021-01-08T11:53:29.668+0000\",\n" +
            "    \"error\": \"Not Found\",\n" +
            "    \"message\": \"Not Found\",\n" +
            "}"),
    UNAUTHORIZED("{\n" +
            "    \"timestamp\": \"2021-01-08T11:53:29.668+0000\",\n" +
            "    \"error\": \"Unauthorized\",\n" +
            "    \"message\": \"Authorization header verification failed\",\n" +
            "    \"internalErrorCode\": \"COMMON_ERROR_GENERIC\",\n" +
            "    \"details\": {\n" +
            "        \"status\": \"UNAUTHENTICATED: Invalid JWT (not before or expired)\",\n" +
            "        \"statusCode\": 401\n" +
            "    }\n" +
            "}"),
    QUERY_RESPONSE("{\n" +
            "    \"data\": [\n" +
            "        {\n" +
            "            \"telephonenumber__c\": \"001 5483188\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"telephonenumber__c\": \"001 3205512\"\n" +
            "        }]," +
            "    \"startTime\": \"2021-01-11T05:34:34.040931Z\",\n" +
            "    \"endTime\": \"2021-01-11T05:34:34.040981Z\",\n" +
            "    \"rowCount\": 2,\n" +
            "    \"queryId\": \"53c66a0f-e666-4f61-9f84-7718528b7a63\",\n" +
            "    \"done\": true," +
            "    \"metadata\": {\n" +
            "        \"telephonenumber__c\": {\n" +
            "            \"placeInOrder\": 0,\n" +
            "            \"typeCode\": 12,\n" +
            "            \"type\": \"VARCHAR\"\n" +
            "        }\n" +
            "} \n" +
            "}"),
    QUERY_RESPONSE_V2("{\n" +
            "    \"data\": [\n" +
            "        [\n" +
            "            \"00034d6c-f5b4-348a-9fc2-7707d5b07dba\",\n" +
            "            \"Larae\"\n" +
            "        ]\n" +
            "    ],\n" +
            "    \"startTime\": \"2021-09-21T10:38:55.520428Z\",\n" +
            "    \"endTime\": \"2021-09-21T10:39:02.995939Z\",\n" +
            "    \"rowCount\": 1,\n" +
            "    \"queryId\": \"20210921_103858_00784_rpkgk\",\n" +
            "    \"nextBatchId\": \"\",\n" +
            "    \"done\": true,\n" +
            "    \"metadata\": {\n" +
            "        \"Id__c\": {\n" +
            "            \"type\": \"VARCHAR\",\n" +
            "            \"placeInOrder\": 0,\n" +
            "            \"typeCode\": 12\n" +
            "        },\n" +
            "        \"FirstName__c\": {\n" +
            "            \"type\": \"VARCHAR\",\n" +
            "            \"placeInOrder\": 1,\n" +
            "            \"typeCode\": 12\n" +
            "        }\n" +
            "    }\n" +
            "}"),
    PAGINATED_RESPONSE_V2("{\n" +
            "    \"data\": [\n" +
            "        [\n" +
            "            \"00034d6c-f5b4-348a-9fc2-7707d5b07dba\",\n" +
            "            \"Larae\"\n" +
            "        ]\n" +
            "    ],\n" +
            "    \"startTime\": \"2021-09-21T10:38:55.520428Z\",\n" +
            "    \"endTime\": \"2021-09-21T10:39:02.995939Z\",\n" +
            "    \"rowCount\": 1,\n" +
            "    \"queryId\": \"20210921_103858_00784_rpkgk\",\n" +
            "    \"nextBatchId\": \"f98c7bcd-b1bd-4e8d-b98d-11aabdd6c604\",\n" +
            "    \"done\": false,\n" +
            "    \"metadata\": {\n" +
            "        \"Id__c\": {\n" +
            "            \"type\": \"VARCHAR\",\n" +
            "            \"placeInOrder\": 0,\n" +
            "            \"typeCode\": 12\n" +
            "        },\n" +
            "        \"FirstName__c\": {\n" +
            "            \"type\": \"VARCHAR\",\n" +
            "            \"placeInOrder\": 1,\n" +
            "            \"typeCode\": 12\n" +
            "        }\n" +
            "    }\n" +
            "}"),
    EMPTY_RESPONSE("{\n" +
            "    \"data\": []," +
            "    \"startTime\": \"2021-01-11T05:34:34.040931Z\",\n" +
            "    \"endTime\": \"2021-01-11T05:34:34.040981Z\",\n" +
            "    \"rowCount\": 0,\n" +
            "    \"queryId\": \"53c66a0f-e666-4f61-9f84-7718528b7a63\",\n" +
            "    \"done\": true}"),
    TOKEN_EXCHANGE("{\n" +
            "    \"access_token\": \"1234\",\n" +
            "    \"instance_url\": \"abcd\",\n" +
            "    \"token_type\": \"Bearer\",\n" +
            "    \"issued_token_type\": \"urn:ietf:params:oauth:token-type:jwt\",\n" +
            "    \"expires_in\": 7193}"),
    TOO_MANY_REQUESTS("{\n" +
            "    \"timestamp\": \"2021-01-08T11:53:29.668+0000\",\n" +
            "    \"error\": \"Too many requests\",\n" +
            "    \"message\": \"Too many requests\",\n" +
            "}"),
    PAGINATION_RESPONSE("{\n" +
            "    \"data\": [\n" +
            "        {\n" +
            "            \"telephonenumber__c\": \"001 6723687\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"telephonenumber__c\": \"001 9387489\"\n" +
            "        }]," +
            "    \"startTime\": \"2021-01-11T05:34:34.040931Z\",\n" +
            "    \"endTime\": \"2021-01-11T05:34:34.040981Z\",\n" +
            "    \"rowCount\": 2,\n" +
            "    \"queryId\": \"53c66a0f-e666-4f61-9f84-7718528b7a63\",\n" +
            "    \"done\": false," +
            "    \"metadata\": {\n" +
            "        \"telephonenumber__c\": {\n" +
            "            \"placeInOrder\": 0,\n" +
            "            \"typeCode\": 12,\n" +
            "            \"type\": \"VARCHAR\"\n" +
            "        }\n" +
            "} \n" +
            "}"),
    RENEWED_CORE_TOKEN("{\n" +
            "    \"access_token\": \"00DR0000000KvIt\",\n" +
            "    \"signature\": \"0w96S+=\",\n" +
            "    \"scope\": \"refresh_token cdpquery api cdpprofile\",\n" +
            "    \"instance_url\": \"https://flash232cdpusercom.my.stmpa.stm.salesforce.com\",\n" +
            "    \"id\": \"https://login.stmpa.stm.salesforce.com/id/00DR0000000KvItMAK/005R0000000pgIjIAI\",\n" +
            "    \"token_type\": \"Bearer\",\n" +
            "    \"issued_at\": \"1611569641915\"\n" +
            "}"),
    OAUTH_TOKEN_ERROR("{\n" +
            "    \"error\": \"invalid_grant\",\n" +
            "    \"error_description\": \"expired authorization code\"\n" +
            "}"),
    QUERY_RESPONSE_WITHOUT_DONE_FLAG("{\n" +
            "    \"data\": [\n" +
            "        {\n" +
            "            \"telephonenumber__c\": \"001 6723687\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"telephonenumber__c\": \"001 9387489\"\n" +
            "        }]," +
            "    \"startTime\": \"2021-01-11T05:34:34.040931Z\",\n" +
            "    \"endTime\": \"2021-01-11T05:34:34.040981Z\",\n" +
            "    \"rowCount\": 2,\n" +
            "    \"queryId\": \"53c66a0f-e666-4f61-9f84-7718528b7a63\"," +
            "    \"metadata\": {\n" +
            "        \"telephonenumber__c\": {\n" +
            "            \"placeInOrder\": 0,\n" +
            "            \"typeCode\": 12,\n" +
            "            \"type\": \"VARCHAR\"\n" +
            "        }\n" +
            "} \n" +
            "}"),
    QUERY_RESPONSE_WITH_METADATA("{\n" +
            "    \"data\": [\n" +
            "        {\n" +
            "            \"count_num\": \"10\"\n" +
            "        }]," +
            "    \"startTime\": \"2021-01-11T05:34:34.040931Z\",\n" +
            "    \"endTime\": \"2021-01-11T05:34:34.040981Z\",\n" +
            "    \"rowCount\": 1,\n" +
            "    \"queryId\": \"53c66a0f-e666-4f61-9f84-7718528b7a63\",\n" +
            "    \"done\": true,\n" +
            "    \"metadata\": {\n" +
                    "        \"count_num\": {\n" +
                    "            \"placeInOrder\": 0,\n" +
                    "            \"typeCode\": 3,\n" +
                    "            \"type\": \"DECIMAL\"\n" +
                    "        }\n" +
                    "} \n" +
            "}"),
    HTML_ERROR_RESPONSE("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n" +
            "<html>\n" +
            "\n" +
            "<head>\n" +
            "\t<meta HTTP-EQUIV=\"PRAGMA\" CONTENT=\"NO-CACHE\">\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\t<script>\n" +
            "\t\tfunction redirectOnLoad() {\n" +
            "if (this.SfdcApp && this.SfdcApp.projectOneNavigator) { SfdcApp.projectOneNavigator.handleRedirect('https://login.stmpb.stm.salesforce.com/?ec=302&startURL=%2Fservices%2Fa360%2Ftoken'); }  else \n" +
            "if (window.location.replace){ \n" +
            "window.location.replace('https://login.stmpb.stm.salesforce.com/?ec=302&startURL=%2Fservices%2Fa360%2Ftoken');\n" +
            "} else {\n" +
            "window.location.href ='https://login.stmpb.stm.salesforce.com/?ec=302&startURL=%2Fservices%2Fa360%2Ftoken';\n" +
            "} \n" +
            "} \n" +
            "redirectOnLoad();\n" +
            "\t</script>\n" +
            "\n" +
            "</head>\n" +
            "\n" +
            "\n" +
            "</html>"),
    DATASPACE_RESPONSE("{\n" +
            "    \"totalSize\": 2,\n" +
            "    \"done\": true,\n" +
            "    \"records\": [\n" +
            "        {\n" +
            "            \"attributes\": {\n" +
            "                \"type\": \"DataSpace\",\n" +
            "                \"url\": \"/services/data/v60.0/sobjects/DataSpace/0vhVF00000003AnYAI\"\n" +
            "            },\n" +
            "            \"Name\": \"default\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"attributes\": {\n" +
            "                \"type\": \"DataSpace\",\n" +
            "                \"url\": \"/services/data/v60.0/sobjects/DataSpace/0vhVF0000000Ch7YAE\"\n" +
            "            },\n" +
            "            \"Name\": \"DS2\"\n" +
            "        }\n" +
            "    ]\n" +
            "}");

    private String response;

    ResponseEnum(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
