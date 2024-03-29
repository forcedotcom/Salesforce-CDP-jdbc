// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: salesforce/cdp/queryservice/v1/query_service.proto

package com.salesforce.a360.queryservice.grpc.v1;

public interface AnsiSqlQueryResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:salesforce.cdp.queryservice.v1.AnsiSqlQueryResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int32 row_count = 1;</code>
   * @return The rowCount.
   */
  int getRowCount();

  /**
   * <code>bool done = 2;</code>
   * @return The done.
   */
  boolean getDone();

  /**
   * <code>string query_id = 3;</code>
   * @return The queryId.
   */
  java.lang.String getQueryId();
  /**
   * <code>string query_id = 3;</code>
   * @return The bytes for queryId.
   */
  com.google.protobuf.ByteString
      getQueryIdBytes();

  /**
   * <code>string start_time = 4;</code>
   * @return The startTime.
   */
  java.lang.String getStartTime();
  /**
   * <code>string start_time = 4;</code>
   * @return The bytes for startTime.
   */
  com.google.protobuf.ByteString
      getStartTimeBytes();

  /**
   * <code>string end_time = 5;</code>
   * @return The endTime.
   */
  java.lang.String getEndTime();
  /**
   * <code>string end_time = 5;</code>
   * @return The bytes for endTime.
   */
  com.google.protobuf.ByteString
      getEndTimeBytes();

  /**
   * <code>.google.protobuf.Struct metadata = 6;</code>
   * @return Whether the metadata field is set.
   */
  boolean hasMetadata();
  /**
   * <code>.google.protobuf.Struct metadata = 6;</code>
   * @return The metadata.
   */
  com.google.protobuf.Struct getMetadata();
  /**
   * <code>.google.protobuf.Struct metadata = 6;</code>
   */
  com.google.protobuf.StructOrBuilder getMetadataOrBuilder();

  /**
   * <code>repeated .google.protobuf.Struct data = 7;</code>
   */
  java.util.List<com.google.protobuf.Struct> 
      getDataList();
  /**
   * <code>repeated .google.protobuf.Struct data = 7;</code>
   */
  com.google.protobuf.Struct getData(int index);
  /**
   * <code>repeated .google.protobuf.Struct data = 7;</code>
   */
  int getDataCount();
  /**
   * <code>repeated .google.protobuf.Struct data = 7;</code>
   */
  java.util.List<? extends com.google.protobuf.StructOrBuilder> 
      getDataOrBuilderList();
  /**
   * <code>repeated .google.protobuf.Struct data = 7;</code>
   */
  com.google.protobuf.StructOrBuilder getDataOrBuilder(
      int index);
}
