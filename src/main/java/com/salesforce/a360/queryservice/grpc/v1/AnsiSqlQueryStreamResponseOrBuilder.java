// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: salesforce/cdp/queryservice/v1/query_service.proto

package com.salesforce.a360.queryservice.grpc.v1;

public interface AnsiSqlQueryStreamResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:salesforce.cdp.queryservice.v1.AnsiSqlQueryStreamResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.salesforce.cdp.queryservice.v1.AnsiSqlQueryStreamMetadata metadata = 1;</code>
   * @return Whether the metadata field is set.
   */
  boolean hasMetadata();
  /**
   * <code>.salesforce.cdp.queryservice.v1.AnsiSqlQueryStreamMetadata metadata = 1;</code>
   * @return The metadata.
   */
  com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryStreamMetadata getMetadata();
  /**
   * <code>.salesforce.cdp.queryservice.v1.AnsiSqlQueryStreamMetadata metadata = 1;</code>
   */
  com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryStreamMetadataOrBuilder getMetadataOrBuilder();

  /**
   * <code>.salesforce.cdp.queryservice.v1.AnsiSqlQueryStreamResponseChunk response_chunk = 2;</code>
   * @return Whether the responseChunk field is set.
   */
  boolean hasResponseChunk();
  /**
   * <code>.salesforce.cdp.queryservice.v1.AnsiSqlQueryStreamResponseChunk response_chunk = 2;</code>
   * @return The responseChunk.
   */
  com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryStreamResponseChunk getResponseChunk();
  /**
   * <code>.salesforce.cdp.queryservice.v1.AnsiSqlQueryStreamResponseChunk response_chunk = 2;</code>
   */
  com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryStreamResponseChunkOrBuilder getResponseChunkOrBuilder();

  /**
   * <code>.salesforce.cdp.queryservice.v1.AnsiSqlQueryStreamArrowResponseChunk arrow_response_chunk = 3;</code>
   * @return Whether the arrowResponseChunk field is set.
   */
  boolean hasArrowResponseChunk();
  /**
   * <code>.salesforce.cdp.queryservice.v1.AnsiSqlQueryStreamArrowResponseChunk arrow_response_chunk = 3;</code>
   * @return The arrowResponseChunk.
   */
  com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryStreamArrowResponseChunk getArrowResponseChunk();
  /**
   * <code>.salesforce.cdp.queryservice.v1.AnsiSqlQueryStreamArrowResponseChunk arrow_response_chunk = 3;</code>
   */
  com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryStreamArrowResponseChunkOrBuilder getArrowResponseChunkOrBuilder();

  public com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryStreamResponse.ResultCase getResultCase();
}
