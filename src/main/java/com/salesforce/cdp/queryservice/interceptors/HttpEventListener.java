package com.salesforce.cdp.queryservice.interceptors;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.LocalDateTime;

@Slf4j
public class HttpEventListener extends EventListener {

    @Override
    public void connectStart(Call call, InetSocketAddress inetSocketAddress, Proxy proxy) {
        log.info("connectStart");
    }

    @Override
    public void connectEnd(Call call, InetSocketAddress inetSocketAddress, Proxy proxy, Protocol protocol) {
        log.info("connectEnd");
    }



    @Override
    public void callStart(Call call) {
        log.info("callStart");
    }

//    @Override
//    public void requestHeadersEnd(Call call, Request request) {
//        log.info("requestHeadersEnd");
//    }
//
//    @Override
//    public void responseHeadersEnd(Call call, Response response) {
//        log.info("responseHeadersEnd");
//    }

    @Override
    public void callEnd(Call call) {
        log.info("callEnd");
    }

    @Override
    public void requestBodyEnd(Call call, long byteCount){
        log.info("requestBodyEnd {}", byteCount);
    }

    @Override
    public void requestBodyStart(Call call) {
        log.info("requestBodyStart");
    }

    @Override
    public void responseBodyStart(Call call) {
        log.info("responseBodyStart");
    }

    @Override
    public void responseBodyEnd(Call call, long byteCount) {
        log.info("responseBodyEnd");
    }
}
