package com.salesforce.cdp.queryservice.interceptors;

import com.salesforce.cdp.queryservice.util.Constants;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.GzipSource;
import okio.Okio;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;

@Slf4j
public class GzipInterceptor implements Interceptor {

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        log.trace("Adding the Accept-Encoding header");
        Request.Builder newRequest = updateRequestWithAcceptEncoding(chain);
        Response response = chain.proceed(newRequest.build());
        if (null != response.headers().get(Constants.CONTENT_ENCODING) &&
                    response.headers().get(Constants.CONTENT_ENCODING).equals(Constants.GZIP_ENCODING)) {
                return decompress(response);
        } else {
           return response;
        }
    }

    private Request.Builder updateRequestWithAcceptEncoding(Chain chain) {
        Request.Builder request= chain.request().newBuilder();
        request.addHeader(Constants.ACCEPT_ENCODING, Constants.GZIP_ENCODING);
        return request;
    }

    private Response decompress(Response response) throws IOException {

        if (null == response.body()) {
            return response;
        }
        log.trace("Decompressing the response body");
        GzipSource gzipSource = new GzipSource(response.body().source());
        String bodyString = Okio.buffer(gzipSource).readUtf8();

        ResponseBody responseBody = ResponseBody.create(bodyString,response.body().contentType());

        Headers headers = response.headers().newBuilder()
                .removeAll(Constants.CONTENT_ENCODING)
                .removeAll(Constants.CONTENT_LENGTH )
                .build();
        return response.newBuilder()
                .headers(headers)
                .body(responseBody)
                .message(response.message())
                .build();
    }
}
