package com.salesforce.cdp.queryservice.interceptors;

import com.salesforce.cdp.queryservice.ResponseEnum;
import com.salesforce.cdp.queryservice.util.Constants;
import okhttp3.*;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;

class GzipInterceptorTest {
    private Interceptor.Chain chain;
    private GzipInterceptor gzipInterceptor;


    void setUp() {
        chain = mock(Interceptor.Chain.class);
        gzipInterceptor = new GzipInterceptor();
        doReturn(buildRequest()).when(chain).request();
    }

    private Request buildRequest() {
        return new Request.Builder()
                .url("https://mjrgg9bzgy2dsyzvmjrgkmzzg1.c360a.salesforce.com" + Constants.CDP_URL + Constants.METADATA_URL)
                .method(Constants.POST, RequestBody.create("{test: test}", MediaType.parse("application/json")))
                .build();
    }

    @Test
    void testInterceptForCompressedRequestBody() throws IOException {
        setUp();
        doReturn(buildCompressedResponse()).when(chain).proceed(any(Request.class));
        Response response=gzipInterceptor.intercept(chain);
        Assert.assertEquals(ResponseEnum.QUERY_RESPONSE.getResponse(),response.body().string());
    }
    @Test
    void testInterceptForNonCompressedRequestBody() throws IOException {
        setUp();
        doReturn(buildNonCompressedResponse(ResponseEnum.QUERY_RESPONSE)).when(chain).proceed(any(Request.class));
        Response response=gzipInterceptor.intercept(chain);
        Assert.assertEquals(ResponseEnum.QUERY_RESPONSE.getResponse(),response.body().string());
    }

    private Object buildNonCompressedResponse(ResponseEnum queryResponse) {
        return new Response.Builder().code(200).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Redirected").
                body(ResponseBody.create(queryResponse.getResponse(),MediaType.parse("application/json"))).
                build();
    }

    private static byte[] compress(String data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        gzip.write(data.getBytes());
        gzip.close();
        byte[] compressed = bos.toByteArray();
        bos.close();
        return compressed;
    }
    private Response buildCompressedResponse() throws IOException {
        byte[] body= compress(ResponseEnum.QUERY_RESPONSE.getResponse());
        return new Response.Builder().code(200).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("test message").
                body(ResponseBody.create(body,MediaType.parse("application/json"))).
                addHeader(Constants.CONTENT_ENCODING,Constants.GZIP_ENCODING).
                build();
    }
}