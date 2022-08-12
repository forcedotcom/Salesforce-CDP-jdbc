package com.salesforce.cdp.queryservice.interceptors;

import com.salesforce.cdp.queryservice.util.Constants;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.MethodDescriptor;

import java.util.Map;
import java.util.Properties;

public class GrpcInterceptor implements ClientInterceptor {

  private final Map<String, String> authHeaders;
  private final Properties properties;

  public GrpcInterceptor(Map<String, String> authHeaders, Properties properties) {
    this.authHeaders = authHeaders;
    this.properties = properties;
  }

  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
      MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {

    return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
        next.newCall(method, callOptions)) {
      @Override
      public void start(final Listener<RespT> responseListener, final Metadata headers) {
        headers.put(
            Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER), authHeaders.get(Constants.ACCESS_TOKEN));

        if (properties.containsKey(Constants.USER_AGENT)) {
          headers.put(Key.of(Constants.USER_AGENT_GRPC, Metadata.ASCII_STRING_MARSHALLER), properties.get(Constants.USER_AGENT).toString());
        } else {
          headers.put(Key.of(Constants.USER_AGENT_GRPC, Metadata.ASCII_STRING_MARSHALLER), Constants.USER_AGENT_VALUE);
        }

        super.start(responseListener, headers);
      }
    };
  }
}
