package com.salesforce.a360.queryservice.grpc.v1;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Query Service gRPC
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.35.1)",
    comments = "Source: salesforce/cdp/queryservice/v1/query_service.proto")
public final class QueryServiceGrpc {

  private QueryServiceGrpc() {}

  public static final String SERVICE_NAME = "salesforce.cdp.queryservice.v1.QueryService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<AnsiSqlQueryRequest,
      AnsiSqlQueryResponse> getAnsiSqlQueryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AnsiSqlQuery",
      requestType = AnsiSqlQueryRequest.class,
      responseType = AnsiSqlQueryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<AnsiSqlQueryRequest,
      AnsiSqlQueryResponse> getAnsiSqlQueryMethod() {
    io.grpc.MethodDescriptor<AnsiSqlQueryRequest, AnsiSqlQueryResponse> getAnsiSqlQueryMethod;
    if ((getAnsiSqlQueryMethod = QueryServiceGrpc.getAnsiSqlQueryMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getAnsiSqlQueryMethod = QueryServiceGrpc.getAnsiSqlQueryMethod) == null) {
          QueryServiceGrpc.getAnsiSqlQueryMethod = getAnsiSqlQueryMethod =
              io.grpc.MethodDescriptor.<AnsiSqlQueryRequest, AnsiSqlQueryResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AnsiSqlQuery"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  AnsiSqlQueryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  AnsiSqlQueryResponse.getDefaultInstance()))
              .setSchemaDescriptor(new QueryServiceMethodDescriptorSupplier("AnsiSqlQuery"))
              .build();
        }
      }
    }
    return getAnsiSqlQueryMethod;
  }

  private static volatile io.grpc.MethodDescriptor<AnsiSqlQueryRequest,
      AnsiSqlQueryResponseV2> getAnsiSqlQueryStreamingMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AnsiSqlQueryStreaming",
      requestType = AnsiSqlQueryRequest.class,
      responseType = AnsiSqlQueryResponseV2.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<AnsiSqlQueryRequest,
      AnsiSqlQueryResponseV2> getAnsiSqlQueryStreamingMethod() {
    io.grpc.MethodDescriptor<AnsiSqlQueryRequest, AnsiSqlQueryResponseV2> getAnsiSqlQueryStreamingMethod;
    if ((getAnsiSqlQueryStreamingMethod = QueryServiceGrpc.getAnsiSqlQueryStreamingMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getAnsiSqlQueryStreamingMethod = QueryServiceGrpc.getAnsiSqlQueryStreamingMethod) == null) {
          QueryServiceGrpc.getAnsiSqlQueryStreamingMethod = getAnsiSqlQueryStreamingMethod =
              io.grpc.MethodDescriptor.<AnsiSqlQueryRequest, AnsiSqlQueryResponseV2>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AnsiSqlQueryStreaming"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  AnsiSqlQueryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  AnsiSqlQueryResponseV2.getDefaultInstance()))
              .setSchemaDescriptor(new QueryServiceMethodDescriptorSupplier("AnsiSqlQueryStreaming"))
              .build();
        }
      }
    }
    return getAnsiSqlQueryStreamingMethod;
  }

  private static volatile io.grpc.MethodDescriptor<AnsiSqlQueryStreamRequest,
      AnsiSqlQueryStreamResponse> getAnsiSqlQueryStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AnsiSqlQueryStream",
      requestType = AnsiSqlQueryStreamRequest.class,
      responseType = AnsiSqlQueryStreamResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<AnsiSqlQueryStreamRequest,
      AnsiSqlQueryStreamResponse> getAnsiSqlQueryStreamMethod() {
    io.grpc.MethodDescriptor<AnsiSqlQueryStreamRequest, AnsiSqlQueryStreamResponse> getAnsiSqlQueryStreamMethod;
    if ((getAnsiSqlQueryStreamMethod = QueryServiceGrpc.getAnsiSqlQueryStreamMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getAnsiSqlQueryStreamMethod = QueryServiceGrpc.getAnsiSqlQueryStreamMethod) == null) {
          QueryServiceGrpc.getAnsiSqlQueryStreamMethod = getAnsiSqlQueryStreamMethod =
              io.grpc.MethodDescriptor.<AnsiSqlQueryStreamRequest, AnsiSqlQueryStreamResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AnsiSqlQueryStream"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  AnsiSqlQueryStreamRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  AnsiSqlQueryStreamResponse.getDefaultInstance()))
              .setSchemaDescriptor(new QueryServiceMethodDescriptorSupplier("AnsiSqlQueryStream"))
              .build();
        }
      }
    }
    return getAnsiSqlQueryStreamMethod;
  }

  private static volatile io.grpc.MethodDescriptor<AnsiSqlExtractQueryRequest,
      AnsiSqlExtractQueryResponse> getAniSqlExtractQueryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AniSqlExtractQuery",
      requestType = AnsiSqlExtractQueryRequest.class,
      responseType = AnsiSqlExtractQueryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<AnsiSqlExtractQueryRequest,
      AnsiSqlExtractQueryResponse> getAniSqlExtractQueryMethod() {
    io.grpc.MethodDescriptor<AnsiSqlExtractQueryRequest, AnsiSqlExtractQueryResponse> getAniSqlExtractQueryMethod;
    if ((getAniSqlExtractQueryMethod = QueryServiceGrpc.getAniSqlExtractQueryMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getAniSqlExtractQueryMethod = QueryServiceGrpc.getAniSqlExtractQueryMethod) == null) {
          QueryServiceGrpc.getAniSqlExtractQueryMethod = getAniSqlExtractQueryMethod =
              io.grpc.MethodDescriptor.<AnsiSqlExtractQueryRequest, AnsiSqlExtractQueryResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AniSqlExtractQuery"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  AnsiSqlExtractQueryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  AnsiSqlExtractQueryResponse.getDefaultInstance()))
              .setSchemaDescriptor(new QueryServiceMethodDescriptorSupplier("AniSqlExtractQuery"))
              .build();
        }
      }
    }
    return getAniSqlExtractQueryMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static QueryServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<QueryServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<QueryServiceStub>() {
        @Override
        public QueryServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new QueryServiceStub(channel, callOptions);
        }
      };
    return QueryServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static QueryServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<QueryServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<QueryServiceBlockingStub>() {
        @Override
        public QueryServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new QueryServiceBlockingStub(channel, callOptions);
        }
      };
    return QueryServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static QueryServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<QueryServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<QueryServiceFutureStub>() {
        @Override
        public QueryServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new QueryServiceFutureStub(channel, callOptions);
        }
      };
    return QueryServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Query Service gRPC
   * </pre>
   */
  public static abstract class QueryServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Query the Salesforce CDP data lake across data model, lake, unified, and linked objects.
     * </pre>
     */
    public void ansiSqlQuery(AnsiSqlQueryRequest request,
                             io.grpc.stub.StreamObserver<AnsiSqlQueryResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAnsiSqlQueryMethod(), responseObserver);
    }

    /**
     */
    public void ansiSqlQueryStreaming(AnsiSqlQueryRequest request,
                                      io.grpc.stub.StreamObserver<AnsiSqlQueryResponseV2> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAnsiSqlQueryStreamingMethod(), responseObserver);
    }

    /**
     * <pre>
     * Public endpoint for Hyper Service
     * </pre>
     */
    public void ansiSqlQueryStream(AnsiSqlQueryStreamRequest request,
                                   io.grpc.stub.StreamObserver<AnsiSqlQueryStreamResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAnsiSqlQueryStreamMethod(), responseObserver);
    }

    /**
     */
    public void aniSqlExtractQuery(AnsiSqlExtractQueryRequest request,
                                   io.grpc.stub.StreamObserver<AnsiSqlExtractQueryResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAniSqlExtractQueryMethod(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getAnsiSqlQueryMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                AnsiSqlQueryRequest,
                AnsiSqlQueryResponse>(
                  this, METHODID_ANSI_SQL_QUERY)))
          .addMethod(
            getAnsiSqlQueryStreamingMethod(),
            io.grpc.stub.ServerCalls.asyncServerStreamingCall(
              new MethodHandlers<
                AnsiSqlQueryRequest,
                AnsiSqlQueryResponseV2>(
                  this, METHODID_ANSI_SQL_QUERY_STREAMING)))
          .addMethod(
            getAnsiSqlQueryStreamMethod(),
            io.grpc.stub.ServerCalls.asyncServerStreamingCall(
              new MethodHandlers<
                AnsiSqlQueryStreamRequest,
                AnsiSqlQueryStreamResponse>(
                  this, METHODID_ANSI_SQL_QUERY_STREAM)))
          .addMethod(
            getAniSqlExtractQueryMethod(),
            io.grpc.stub.ServerCalls.asyncServerStreamingCall(
              new MethodHandlers<
                AnsiSqlExtractQueryRequest,
                AnsiSqlExtractQueryResponse>(
                  this, METHODID_ANI_SQL_EXTRACT_QUERY)))
          .build();
    }
  }

  /**
   * <pre>
   * Query Service gRPC
   * </pre>
   */
  public static final class QueryServiceStub extends io.grpc.stub.AbstractAsyncStub<QueryServiceStub> {
    private QueryServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected QueryServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new QueryServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Query the Salesforce CDP data lake across data model, lake, unified, and linked objects.
     * </pre>
     */
    public void ansiSqlQuery(AnsiSqlQueryRequest request,
                             io.grpc.stub.StreamObserver<AnsiSqlQueryResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAnsiSqlQueryMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void ansiSqlQueryStreaming(AnsiSqlQueryRequest request,
                                      io.grpc.stub.StreamObserver<AnsiSqlQueryResponseV2> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getAnsiSqlQueryStreamingMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Public endpoint for Hyper Service
     * </pre>
     */
    public void ansiSqlQueryStream(AnsiSqlQueryStreamRequest request,
                                   io.grpc.stub.StreamObserver<AnsiSqlQueryStreamResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getAnsiSqlQueryStreamMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void aniSqlExtractQuery(AnsiSqlExtractQueryRequest request,
                                   io.grpc.stub.StreamObserver<AnsiSqlExtractQueryResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getAniSqlExtractQueryMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * Query Service gRPC
   * </pre>
   */
  public static final class QueryServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<QueryServiceBlockingStub> {
    private QueryServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected QueryServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new QueryServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Query the Salesforce CDP data lake across data model, lake, unified, and linked objects.
     * </pre>
     */
    public AnsiSqlQueryResponse ansiSqlQuery(AnsiSqlQueryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAnsiSqlQueryMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<AnsiSqlQueryResponseV2> ansiSqlQueryStreaming(
        AnsiSqlQueryRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getAnsiSqlQueryStreamingMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Public endpoint for Hyper Service
     * </pre>
     */
    public java.util.Iterator<AnsiSqlQueryStreamResponse> ansiSqlQueryStream(
        AnsiSqlQueryStreamRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getAnsiSqlQueryStreamMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<AnsiSqlExtractQueryResponse> aniSqlExtractQuery(
        AnsiSqlExtractQueryRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getAniSqlExtractQueryMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * Query Service gRPC
   * </pre>
   */
  public static final class QueryServiceFutureStub extends io.grpc.stub.AbstractFutureStub<QueryServiceFutureStub> {
    private QueryServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected QueryServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new QueryServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Query the Salesforce CDP data lake across data model, lake, unified, and linked objects.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<AnsiSqlQueryResponse> ansiSqlQuery(
        AnsiSqlQueryRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAnsiSqlQueryMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_ANSI_SQL_QUERY = 0;
  private static final int METHODID_ANSI_SQL_QUERY_STREAMING = 1;
  private static final int METHODID_ANSI_SQL_QUERY_STREAM = 2;
  private static final int METHODID_ANI_SQL_EXTRACT_QUERY = 3;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final QueryServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(QueryServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ANSI_SQL_QUERY:
          serviceImpl.ansiSqlQuery((AnsiSqlQueryRequest) request,
              (io.grpc.stub.StreamObserver<AnsiSqlQueryResponse>) responseObserver);
          break;
        case METHODID_ANSI_SQL_QUERY_STREAMING:
          serviceImpl.ansiSqlQueryStreaming((AnsiSqlQueryRequest) request,
              (io.grpc.stub.StreamObserver<AnsiSqlQueryResponseV2>) responseObserver);
          break;
        case METHODID_ANSI_SQL_QUERY_STREAM:
          serviceImpl.ansiSqlQueryStream((AnsiSqlQueryStreamRequest) request,
              (io.grpc.stub.StreamObserver<AnsiSqlQueryStreamResponse>) responseObserver);
          break;
        case METHODID_ANI_SQL_EXTRACT_QUERY:
          serviceImpl.aniSqlExtractQuery((AnsiSqlExtractQueryRequest) request,
              (io.grpc.stub.StreamObserver<AnsiSqlExtractQueryResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class QueryServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    QueryServiceBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return QueryServiceProto.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("QueryService");
    }
  }

  private static final class QueryServiceFileDescriptorSupplier
      extends QueryServiceBaseDescriptorSupplier {
    QueryServiceFileDescriptorSupplier() {}
  }

  private static final class QueryServiceMethodDescriptorSupplier
      extends QueryServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    QueryServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (QueryServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new QueryServiceFileDescriptorSupplier())
              .addMethod(getAnsiSqlQueryMethod())
              .addMethod(getAnsiSqlQueryStreamingMethod())
              .addMethod(getAnsiSqlQueryStreamMethod())
              .addMethod(getAniSqlExtractQueryMethod())
              .build();
        }
      }
    }
    return result;
  }
}
