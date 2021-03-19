package com.salesforce.cdp.queryservice.interceptors;

import com.salesforce.cdp.queryservice.ResponseEnum;
import com.salesforce.cdp.queryservice.util.Constants;
import okhttp3.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.salesforce.cdp.queryservice.ResponseEnum.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class RetryInterceptorTest {

    private Interceptor.Chain chain;

    private RetryInterceptor retryInterceptor;

    @Before
    public void init() {
        chain = mock(Interceptor.Chain.class);
        retryInterceptor = new RetryInterceptor();
        doReturn(buildRequest()).when(chain).request();
    }

    @Test
    public void testRetryFor302Response() throws IOException {
        doReturn(buildResponse(302, EMPTY_RESPONSE)).doReturn(buildResponse(200, QUERY_RESPONSE)).when(chain).proceed(any(Request.class));
        retryInterceptor.intercept(chain);
        verify(chain, times(2)).proceed(any(Request.class));
    }

    @Test
    public void testNoRetryForSuccessfulResponse() throws IOException {
        doReturn(buildResponse(200, QUERY_RESPONSE)).when(chain).proceed(any(Request.class));
        retryInterceptor.intercept(chain);
        verify(chain, times(1)).proceed(any(Request.class));
    }

    @Test
    public void testMaxRetryForThrottledRequest() throws IOException {
        doReturn(buildResponse(429, TOO_MANY_REQUESTS)).when(chain).proceed(any(Request.class));
        Response response = retryInterceptor.intercept(chain);
        verify(chain, times(4)).proceed(any(Request.class));
        Assert.assertEquals(response.code(), 429);
    }

    private Request buildRequest() {
        return new Request.Builder()
                .url("https://mjrgg9bzgy2dsyzvmjrgkmzzg1.c360a.salesforce.com" + Constants.CDP_URL + Constants.ANSI_SQL_URL)
                .method(Constants.POST, RequestBody.create("{test: test}", MediaType.parse("application/json")))
                .build();
    }

    private Response buildResponse(int statusCode, ResponseEnum responseEnum) {
        String jsonString = responseEnum.getResponse();
        Response response = new Response.Builder().code(statusCode).
                request(buildRequest()).protocol(Protocol.HTTP_1_1).
                message("Redirected").
                body(ResponseBody.create(jsonString, MediaType.parse("application/json"))).build();
        return response;
    }
}
