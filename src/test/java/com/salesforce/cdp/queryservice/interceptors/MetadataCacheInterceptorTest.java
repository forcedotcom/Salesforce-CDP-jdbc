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

import com.salesforce.cdp.queryservice.ResponseEnum;
import com.salesforce.cdp.queryservice.util.Constants;
import com.salesforce.cdp.queryservice.util.MetadataCacheUtil;
import okhttp3.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.salesforce.cdp.queryservice.ResponseEnum.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class MetadataCacheInterceptorTest {

    private Interceptor.Chain chain;

    private MetadataCacheInterceptor metadataCacheInterceptor;

    @Before
    public void init() {
        chain = mock(Interceptor.Chain.class);
        metadataCacheInterceptor = new MetadataCacheInterceptor();
        doReturn(buildRequest()).when(chain).request();
    }

    @Test
    public void testMetadataRequestWithNoCachePresent() throws IOException {
        doReturn(buildResponse(200, EMPTY_RESPONSE)).doReturn(buildResponse(200, QUERY_RESPONSE)).when(chain).proceed(any(Request.class));
        metadataCacheInterceptor.intercept(chain);
        verify(chain, times(1)).proceed(any(Request.class));
    }

    @Test
    public void testMetadataFromCache()  throws IOException {
        MetadataCacheUtil.cacheMetadata("https://mjrgg9bzgy2dsyzvmjrgkmzzg1.c360a.salesforce.com" + Constants.CDP_URL + Constants.METADATA_URL, TABLE_METADATA.getResponse());
        metadataCacheInterceptor.intercept(chain);
        verify(chain, times(0)).proceed(any(Request.class));
    }

    private Request buildRequest() {
        return new Request.Builder()
                .url("https://mjrgg9bzgy2dsyzvmjrgkmzzg1.c360a.salesforce.com" + Constants.CDP_URL + Constants.METADATA_URL)
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
