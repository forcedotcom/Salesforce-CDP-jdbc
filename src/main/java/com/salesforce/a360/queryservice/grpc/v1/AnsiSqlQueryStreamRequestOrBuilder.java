// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: salesforce/cdp/queryservice/v1/query_service.proto

package com.salesforce.a360.queryservice.grpc.v1;

public interface AnsiSqlQueryStreamRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:salesforce.cdp.queryservice.v1.AnsiSqlQueryStreamRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string query = 1;</code>
   * @return The query.
   */
  String getQuery();
  /**
   * <code>string query = 1;</code>
   * @return The bytes for query.
   */
  com.google.protobuf.ByteString
      getQueryBytes();

  /**
   * <code>string tenantId = 2;</code>
   * @return The tenantId.
   */
  String getTenantId();
  /**
   * <code>string tenantId = 2;</code>
   * @return The bytes for tenantId.
   */
  com.google.protobuf.ByteString
      getTenantIdBytes();
}
