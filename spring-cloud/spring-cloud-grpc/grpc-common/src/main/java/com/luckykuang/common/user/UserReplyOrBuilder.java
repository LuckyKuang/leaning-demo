// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: user.proto

package com.luckykuang.common.user;

public interface UserReplyOrBuilder extends
    // @@protoc_insertion_point(interface_extends:UserReply)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int32 code = 1;</code>
   * @return The code.
   */
  int getCode();

  /**
   * <code>string msg = 2;</code>
   * @return The msg.
   */
  java.lang.String getMsg();
  /**
   * <code>string msg = 2;</code>
   * @return The bytes for msg.
   */
  com.google.protobuf.ByteString
      getMsgBytes();

  /**
   * <code>bool success = 3;</code>
   * @return The success.
   */
  boolean getSuccess();

  /**
   * <code>.UserReply.Data data = 4;</code>
   * @return Whether the data field is set.
   */
  boolean hasData();
  /**
   * <code>.UserReply.Data data = 4;</code>
   * @return The data.
   */
  com.luckykuang.common.user.UserReply.Data getData();
  /**
   * <code>.UserReply.Data data = 4;</code>
   */
  com.luckykuang.common.user.UserReply.DataOrBuilder getDataOrBuilder();
}
