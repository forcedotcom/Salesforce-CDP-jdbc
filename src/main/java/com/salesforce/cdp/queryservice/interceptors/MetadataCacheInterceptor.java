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

package com.salesforce.cdp.queryservice.interceptors;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.salesforce.cdp.queryservice.util.Constants;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MetadataCacheInterceptor implements Interceptor {
    private final Cache<String, String> metaDataCache;

    public MetadataCacheInterceptor(int metaDataCacheDurationInMs) {
        this.metaDataCache = CacheBuilder.newBuilder()
                .expireAfterWrite(metaDataCacheDurationInMs, TimeUnit.MILLISECONDS)
                .maximumSize(10).build();
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        String cacheKey = request.url().toString();
        Response response;
        String responseString = metaDataCache.getIfPresent(cacheKey);

        Response.Builder responseBuilder = new Response.Builder().code(HttpStatus.SC_OK).
                request(request).protocol(Protocol.HTTP_1_1).
                message("OK");

        if (responseString != null) {
            log.trace("Getting the metadata response from local cache");
        } else {
            log.trace("Cache miss for metadata response. Getting from server");
            response = chain.proceed(request);

            if(!response.isSuccessful()) {
                return response;
            } else {
                log.info("Caching the response");
                responseString = response.body().string();
                metaDataCache.put(cacheKey, responseString);
            }
        }

        responseBuilder.body(ResponseBody.create(responseString, MediaType.parse(Constants.JSON_CONTENT)));
        return responseBuilder.build();
    }
}
