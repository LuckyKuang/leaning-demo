// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: user.proto

package com.luckykuang.common.user;

public final class UserProto {
  private UserProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_UserRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_UserRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_UserReply_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_UserReply_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_UserReply_Data_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_UserReply_Data_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_UserPb_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_UserPb_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\nuser.proto\"\031\n\013UserRequest\022\n\n\002id\030\002 \001(\003\"" +
      "w\n\tUserReply\022\014\n\004code\030\001 \001(\005\022\013\n\003msg\030\002 \001(\t\022" +
      "\017\n\007success\030\003 \001(\010\022\035\n\004data\030\004 \001(\0132\017.UserRep" +
      "ly.Data\032\037\n\004Data\022\027\n\006userPb\030\001 \001(\0132\007.UserPb" +
      "\"/\n\006UserPb\022\n\n\002id\030\001 \001(\003\022\014\n\004name\030\002 \001(\t\022\013\n\003" +
      "sex\030\003 \001(\t26\n\013UserService\022\'\n\tqueryUser\022\014." +
      "UserRequest\032\n.UserReply\"\000B)\n\032com.luckyku" +
      "ang.common.userB\tUserProtoP\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_UserRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_UserRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_UserRequest_descriptor,
        new java.lang.String[] { "Id", });
    internal_static_UserReply_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_UserReply_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_UserReply_descriptor,
        new java.lang.String[] { "Code", "Msg", "Success", "Data", });
    internal_static_UserReply_Data_descriptor =
      internal_static_UserReply_descriptor.getNestedTypes().get(0);
    internal_static_UserReply_Data_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_UserReply_Data_descriptor,
        new java.lang.String[] { "UserPb", });
    internal_static_UserPb_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_UserPb_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_UserPb_descriptor,
        new java.lang.String[] { "Id", "Name", "Sex", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
