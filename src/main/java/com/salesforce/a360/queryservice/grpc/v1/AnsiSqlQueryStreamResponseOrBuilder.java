// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: salesforce/cdp/queryservice/v1/query_service.proto

package com.salesforce.a360.queryservice.grpc.v1;

public interface AnsiSqlQueryStreamResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:salesforce.cdp.queryservice.v1.AnsiSqlQueryStreamResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.google.protobuf.Struct metadata = 1;</code>
   * @return Whether the metadata field is set.
   */
  boolean hasMetadata();
  /**
   * <code>.google.protobuf.Struct metadata = 1;</code>
   * @return The metadata.
   */
  com.google.protobuf.Struct getMetadata();
  /**
   * <code>.google.protobuf.Struct metadata = 1;</code>
   */
  com.google.protobuf.StructOrBuilder getMetadataOrBuilder();

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

  public com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryStreamResponse.ResultCase getResultCase();
}
