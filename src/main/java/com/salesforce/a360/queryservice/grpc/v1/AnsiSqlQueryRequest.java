// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: salesforce/cdp/queryservice/v1/query_service.proto

package com.salesforce.a360.queryservice.grpc.v1;

/**
 * Protobuf type {@code salesforce.cdp.queryservice.v1.AnsiSqlQueryRequest}
 */
public final class AnsiSqlQueryRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:salesforce.cdp.queryservice.v1.AnsiSqlQueryRequest)
    AnsiSqlQueryRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use AnsiSqlQueryRequest.newBuilder() to construct.
  private AnsiSqlQueryRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private AnsiSqlQueryRequest() {
    query_ = "";
    orderbys_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    tenantId_ = "";
    dataspaceName_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new AnsiSqlQueryRequest();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private AnsiSqlQueryRequest(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    int mutable_bitField0_ = 0;
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 10: {
            java.lang.String s = input.readStringRequireUtf8();

            query_ = s;
            break;
          }
          case 16: {

            limit_ = input.readInt32();
            break;
          }
          case 24: {

            offset_ = input.readInt32();
            break;
          }
          case 34: {
            java.lang.String s = input.readStringRequireUtf8();
            if (!((mutable_bitField0_ & 0x00000001) != 0)) {
              orderbys_ = new com.google.protobuf.LazyStringArrayList();
              mutable_bitField0_ |= 0x00000001;
            }
            orderbys_.add(s);
            break;
          }
          case 42: {
            java.lang.String s = input.readStringRequireUtf8();

            tenantId_ = s;
            break;
          }
          case 50: {
            java.lang.String s = input.readStringRequireUtf8();

            dataspaceName_ = s;
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      if (((mutable_bitField0_ & 0x00000001) != 0)) {
        orderbys_ = orderbys_.getUnmodifiableView();
      }
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.salesforce.a360.queryservice.grpc.v1.QueryServiceProto.internal_static_salesforce_cdp_queryservice_v1_AnsiSqlQueryRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.salesforce.a360.queryservice.grpc.v1.QueryServiceProto.internal_static_salesforce_cdp_queryservice_v1_AnsiSqlQueryRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest.class, com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest.Builder.class);
  }

  public static final int QUERY_FIELD_NUMBER = 1;
  private volatile java.lang.Object query_;
  /**
   * <code>string query = 1;</code>
   * @return The query.
   */
  @java.lang.Override
  public java.lang.String getQuery() {
    java.lang.Object ref = query_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      query_ = s;
      return s;
    }
  }
  /**
   * <code>string query = 1;</code>
   * @return The bytes for query.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getQueryBytes() {
    java.lang.Object ref = query_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      query_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int LIMIT_FIELD_NUMBER = 2;
  private int limit_;
  /**
   * <code>int32 limit = 2;</code>
   * @return The limit.
   */
  @java.lang.Override
  public int getLimit() {
    return limit_;
  }

  public static final int OFFSET_FIELD_NUMBER = 3;
  private int offset_;
  /**
   * <code>int32 offset = 3;</code>
   * @return The offset.
   */
  @java.lang.Override
  public int getOffset() {
    return offset_;
  }

  public static final int ORDERBYS_FIELD_NUMBER = 4;
  private com.google.protobuf.LazyStringList orderbys_;
  /**
   * <code>repeated string orderbys = 4;</code>
   * @return A list containing the orderbys.
   */
  public com.google.protobuf.ProtocolStringList
      getOrderbysList() {
    return orderbys_;
  }
  /**
   * <code>repeated string orderbys = 4;</code>
   * @return The count of orderbys.
   */
  public int getOrderbysCount() {
    return orderbys_.size();
  }
  /**
   * <code>repeated string orderbys = 4;</code>
   * @param index The index of the element to return.
   * @return The orderbys at the given index.
   */
  public java.lang.String getOrderbys(int index) {
    return orderbys_.get(index);
  }
  /**
   * <code>repeated string orderbys = 4;</code>
   * @param index The index of the value to return.
   * @return The bytes of the orderbys at the given index.
   */
  public com.google.protobuf.ByteString
      getOrderbysBytes(int index) {
    return orderbys_.getByteString(index);
  }

  public static final int TENANT_ID_FIELD_NUMBER = 5;
  private volatile java.lang.Object tenantId_;
  /**
   * <code>string tenant_id = 5;</code>
   * @return The tenantId.
   */
  @java.lang.Override
  public java.lang.String getTenantId() {
    java.lang.Object ref = tenantId_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      tenantId_ = s;
      return s;
    }
  }
  /**
   * <code>string tenant_id = 5;</code>
   * @return The bytes for tenantId.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getTenantIdBytes() {
    java.lang.Object ref = tenantId_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      tenantId_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int DATASPACE_NAME_FIELD_NUMBER = 6;
  private volatile java.lang.Object dataspaceName_;
  /**
   * <code>string dataspace_name = 6;</code>
   * @return The dataspaceName.
   */
  @java.lang.Override
  public java.lang.String getDataspaceName() {
    java.lang.Object ref = dataspaceName_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      dataspaceName_ = s;
      return s;
    }
  }
  /**
   * <code>string dataspace_name = 6;</code>
   * @return The bytes for dataspaceName.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getDataspaceNameBytes() {
    java.lang.Object ref = dataspaceName_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      dataspaceName_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (!getQueryBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, query_);
    }
    if (limit_ != 0) {
      output.writeInt32(2, limit_);
    }
    if (offset_ != 0) {
      output.writeInt32(3, offset_);
    }
    for (int i = 0; i < orderbys_.size(); i++) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 4, orderbys_.getRaw(i));
    }
    if (!getTenantIdBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 5, tenantId_);
    }
    if (!getDataspaceNameBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 6, dataspaceName_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getQueryBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, query_);
    }
    if (limit_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(2, limit_);
    }
    if (offset_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(3, offset_);
    }
    {
      int dataSize = 0;
      for (int i = 0; i < orderbys_.size(); i++) {
        dataSize += computeStringSizeNoTag(orderbys_.getRaw(i));
      }
      size += dataSize;
      size += 1 * getOrderbysList().size();
    }
    if (!getTenantIdBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(5, tenantId_);
    }
    if (!getDataspaceNameBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(6, dataspaceName_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest)) {
      return super.equals(obj);
    }
    com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest other = (com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest) obj;

    if (!getQuery()
        .equals(other.getQuery())) return false;
    if (getLimit()
        != other.getLimit()) return false;
    if (getOffset()
        != other.getOffset()) return false;
    if (!getOrderbysList()
        .equals(other.getOrderbysList())) return false;
    if (!getTenantId()
        .equals(other.getTenantId())) return false;
    if (!getDataspaceName()
        .equals(other.getDataspaceName())) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + QUERY_FIELD_NUMBER;
    hash = (53 * hash) + getQuery().hashCode();
    hash = (37 * hash) + LIMIT_FIELD_NUMBER;
    hash = (53 * hash) + getLimit();
    hash = (37 * hash) + OFFSET_FIELD_NUMBER;
    hash = (53 * hash) + getOffset();
    if (getOrderbysCount() > 0) {
      hash = (37 * hash) + ORDERBYS_FIELD_NUMBER;
      hash = (53 * hash) + getOrderbysList().hashCode();
    }
    hash = (37 * hash) + TENANT_ID_FIELD_NUMBER;
    hash = (53 * hash) + getTenantId().hashCode();
    hash = (37 * hash) + DATASPACE_NAME_FIELD_NUMBER;
    hash = (53 * hash) + getDataspaceName().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code salesforce.cdp.queryservice.v1.AnsiSqlQueryRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:salesforce.cdp.queryservice.v1.AnsiSqlQueryRequest)
      com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.salesforce.a360.queryservice.grpc.v1.QueryServiceProto.internal_static_salesforce_cdp_queryservice_v1_AnsiSqlQueryRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.salesforce.a360.queryservice.grpc.v1.QueryServiceProto.internal_static_salesforce_cdp_queryservice_v1_AnsiSqlQueryRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest.class, com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest.Builder.class);
    }

    // Construct using com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      query_ = "";

      limit_ = 0;

      offset_ = 0;

      orderbys_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000001);
      tenantId_ = "";

      dataspaceName_ = "";

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.salesforce.a360.queryservice.grpc.v1.QueryServiceProto.internal_static_salesforce_cdp_queryservice_v1_AnsiSqlQueryRequest_descriptor;
    }

    @java.lang.Override
    public com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest getDefaultInstanceForType() {
      return com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest.getDefaultInstance();
    }

    @java.lang.Override
    public com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest build() {
      com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest buildPartial() {
      com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest result = new com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest(this);
      int from_bitField0_ = bitField0_;
      result.query_ = query_;
      result.limit_ = limit_;
      result.offset_ = offset_;
      if (((bitField0_ & 0x00000001) != 0)) {
        orderbys_ = orderbys_.getUnmodifiableView();
        bitField0_ = (bitField0_ & ~0x00000001);
      }
      result.orderbys_ = orderbys_;
      result.tenantId_ = tenantId_;
      result.dataspaceName_ = dataspaceName_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest) {
        return mergeFrom((com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest other) {
      if (other == com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest.getDefaultInstance()) return this;
      if (!other.getQuery().isEmpty()) {
        query_ = other.query_;
        onChanged();
      }
      if (other.getLimit() != 0) {
        setLimit(other.getLimit());
      }
      if (other.getOffset() != 0) {
        setOffset(other.getOffset());
      }
      if (!other.orderbys_.isEmpty()) {
        if (orderbys_.isEmpty()) {
          orderbys_ = other.orderbys_;
          bitField0_ = (bitField0_ & ~0x00000001);
        } else {
          ensureOrderbysIsMutable();
          orderbys_.addAll(other.orderbys_);
        }
        onChanged();
      }
      if (!other.getTenantId().isEmpty()) {
        tenantId_ = other.tenantId_;
        onChanged();
      }
      if (!other.getDataspaceName().isEmpty()) {
        dataspaceName_ = other.dataspaceName_;
        onChanged();
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private java.lang.Object query_ = "";
    /**
     * <code>string query = 1;</code>
     * @return The query.
     */
    public java.lang.String getQuery() {
      java.lang.Object ref = query_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        query_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string query = 1;</code>
     * @return The bytes for query.
     */
    public com.google.protobuf.ByteString
        getQueryBytes() {
      java.lang.Object ref = query_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        query_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string query = 1;</code>
     * @param value The query to set.
     * @return This builder for chaining.
     */
    public Builder setQuery(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      query_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string query = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearQuery() {
      
      query_ = getDefaultInstance().getQuery();
      onChanged();
      return this;
    }
    /**
     * <code>string query = 1;</code>
     * @param value The bytes for query to set.
     * @return This builder for chaining.
     */
    public Builder setQueryBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      query_ = value;
      onChanged();
      return this;
    }

    private int limit_ ;
    /**
     * <code>int32 limit = 2;</code>
     * @return The limit.
     */
    @java.lang.Override
    public int getLimit() {
      return limit_;
    }
    /**
     * <code>int32 limit = 2;</code>
     * @param value The limit to set.
     * @return This builder for chaining.
     */
    public Builder setLimit(int value) {
      
      limit_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 limit = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearLimit() {
      
      limit_ = 0;
      onChanged();
      return this;
    }

    private int offset_ ;
    /**
     * <code>int32 offset = 3;</code>
     * @return The offset.
     */
    @java.lang.Override
    public int getOffset() {
      return offset_;
    }
    /**
     * <code>int32 offset = 3;</code>
     * @param value The offset to set.
     * @return This builder for chaining.
     */
    public Builder setOffset(int value) {
      
      offset_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 offset = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearOffset() {
      
      offset_ = 0;
      onChanged();
      return this;
    }

    private com.google.protobuf.LazyStringList orderbys_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    private void ensureOrderbysIsMutable() {
      if (!((bitField0_ & 0x00000001) != 0)) {
        orderbys_ = new com.google.protobuf.LazyStringArrayList(orderbys_);
        bitField0_ |= 0x00000001;
       }
    }
    /**
     * <code>repeated string orderbys = 4;</code>
     * @return A list containing the orderbys.
     */
    public com.google.protobuf.ProtocolStringList
        getOrderbysList() {
      return orderbys_.getUnmodifiableView();
    }
    /**
     * <code>repeated string orderbys = 4;</code>
     * @return The count of orderbys.
     */
    public int getOrderbysCount() {
      return orderbys_.size();
    }
    /**
     * <code>repeated string orderbys = 4;</code>
     * @param index The index of the element to return.
     * @return The orderbys at the given index.
     */
    public java.lang.String getOrderbys(int index) {
      return orderbys_.get(index);
    }
    /**
     * <code>repeated string orderbys = 4;</code>
     * @param index The index of the value to return.
     * @return The bytes of the orderbys at the given index.
     */
    public com.google.protobuf.ByteString
        getOrderbysBytes(int index) {
      return orderbys_.getByteString(index);
    }
    /**
     * <code>repeated string orderbys = 4;</code>
     * @param index The index to set the value at.
     * @param value The orderbys to set.
     * @return This builder for chaining.
     */
    public Builder setOrderbys(
        int index, java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  ensureOrderbysIsMutable();
      orderbys_.set(index, value);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string orderbys = 4;</code>
     * @param value The orderbys to add.
     * @return This builder for chaining.
     */
    public Builder addOrderbys(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  ensureOrderbysIsMutable();
      orderbys_.add(value);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string orderbys = 4;</code>
     * @param values The orderbys to add.
     * @return This builder for chaining.
     */
    public Builder addAllOrderbys(
        java.lang.Iterable<java.lang.String> values) {
      ensureOrderbysIsMutable();
      com.google.protobuf.AbstractMessageLite.Builder.addAll(
          values, orderbys_);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string orderbys = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearOrderbys() {
      orderbys_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      bitField0_ = (bitField0_ & ~0x00000001);
      onChanged();
      return this;
    }
    /**
     * <code>repeated string orderbys = 4;</code>
     * @param value The bytes of the orderbys to add.
     * @return This builder for chaining.
     */
    public Builder addOrderbysBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      ensureOrderbysIsMutable();
      orderbys_.add(value);
      onChanged();
      return this;
    }

    private java.lang.Object tenantId_ = "";
    /**
     * <code>string tenant_id = 5;</code>
     * @return The tenantId.
     */
    public java.lang.String getTenantId() {
      java.lang.Object ref = tenantId_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        tenantId_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string tenant_id = 5;</code>
     * @return The bytes for tenantId.
     */
    public com.google.protobuf.ByteString
        getTenantIdBytes() {
      java.lang.Object ref = tenantId_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        tenantId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string tenant_id = 5;</code>
     * @param value The tenantId to set.
     * @return This builder for chaining.
     */
    public Builder setTenantId(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      tenantId_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string tenant_id = 5;</code>
     * @return This builder for chaining.
     */
    public Builder clearTenantId() {
      
      tenantId_ = getDefaultInstance().getTenantId();
      onChanged();
      return this;
    }
    /**
     * <code>string tenant_id = 5;</code>
     * @param value The bytes for tenantId to set.
     * @return This builder for chaining.
     */
    public Builder setTenantIdBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      tenantId_ = value;
      onChanged();
      return this;
    }

    private java.lang.Object dataspaceName_ = "";
    /**
     * <code>string dataspace_name = 6;</code>
     * @return The dataspaceName.
     */
    public java.lang.String getDataspaceName() {
      java.lang.Object ref = dataspaceName_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        dataspaceName_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string dataspace_name = 6;</code>
     * @return The bytes for dataspaceName.
     */
    public com.google.protobuf.ByteString
        getDataspaceNameBytes() {
      java.lang.Object ref = dataspaceName_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        dataspaceName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string dataspace_name = 6;</code>
     * @param value The dataspaceName to set.
     * @return This builder for chaining.
     */
    public Builder setDataspaceName(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      dataspaceName_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string dataspace_name = 6;</code>
     * @return This builder for chaining.
     */
    public Builder clearDataspaceName() {
      
      dataspaceName_ = getDefaultInstance().getDataspaceName();
      onChanged();
      return this;
    }
    /**
     * <code>string dataspace_name = 6;</code>
     * @param value The bytes for dataspaceName to set.
     * @return This builder for chaining.
     */
    public Builder setDataspaceNameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      dataspaceName_ = value;
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:salesforce.cdp.queryservice.v1.AnsiSqlQueryRequest)
  }

  // @@protoc_insertion_point(class_scope:salesforce.cdp.queryservice.v1.AnsiSqlQueryRequest)
  private static final com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest();
  }

  public static com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<AnsiSqlQueryRequest>
      PARSER = new com.google.protobuf.AbstractParser<AnsiSqlQueryRequest>() {
    @java.lang.Override
    public AnsiSqlQueryRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new AnsiSqlQueryRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<AnsiSqlQueryRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<AnsiSqlQueryRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.salesforce.a360.queryservice.grpc.v1.AnsiSqlQueryRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

