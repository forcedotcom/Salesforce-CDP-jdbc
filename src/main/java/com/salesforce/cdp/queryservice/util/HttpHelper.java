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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.collections4.MapUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@Slf4j
public class HttpHelper {

    public static void handleErrorResponse(Response response, String propertyName) throws IOException, SQLException {
        handleErrorResponse(response.body().string(), propertyName);
    }

    public static void handleErrorResponse(String response, String propertyName) throws IOException, SQLException {
        ObjectNode node = new ObjectMapper().readValue(response, ObjectNode.class);
        JsonNode jsonNode = node.get(propertyName);
        String message = jsonNode == null ? String.format("Property %s is not defined", propertyName) : node.get(propertyName).asText();
        throw new SQLException(message);
    }

    public static <T> T handleSuccessResponse(String responseString, Class<T> type) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(responseString, type);
    }

    public static <T> T handleSuccessResponse(Response response, Class<T> type, boolean cacheResponse) throws IOException {
        if (response.headers().get("from-local-cache") == null && cacheResponse) {
            log.info("Caching the response");
            MetadataCacheUtil.cacheMetadata(response.request().url().toString(), response.body().string());
        }

        return handleSuccessResponse(response.body().string(), type);
    }

    protected static Request buildRequest(String method, String url, RequestBody body, Map<String, String> headers) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .method(method, body == null ? null : body);
        if (!MapUtils.isEmpty(headers)) {
            for (String key : headers.keySet()) {
                builder.addHeader(key, headers.get(key));
            }
        }
        if (!headers.containsKey(Constants.USER_AGENT)) {
            builder.addHeader(Constants.USER_AGENT, Constants.USER_AGENT_VALUE);
        }
        return builder.build();
    }
}
