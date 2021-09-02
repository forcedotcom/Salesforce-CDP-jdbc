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

import com.salesforce.cdp.queryservice.util.Utils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@Slf4j
public class RetryInterceptor implements Interceptor {

    private static final Integer DEFAULT_RETRY_COUNT = 3;

    private final int maxRetryCount;

    public RetryInterceptor() {
        this(DEFAULT_RETRY_COUNT);
    }

    public RetryInterceptor(int maxRetryCount) {
        this.maxRetryCount =  maxRetryCount;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        int retryCount = 1;
        //TODO: Make it expo backoff if needed.
        while (Utils.getRetryStatusCodes().contains(response.code()) && retryCount <= maxRetryCount) {
            log.error("Request failed with response code {}. Number of request retry {}", response.code(), retryCount);
            retryCount++;
            response.close();
            response = chain.proceed(request);
        }
        return response;
    }
}
