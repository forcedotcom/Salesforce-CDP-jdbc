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

    private int maxRetryCount = 3;

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
