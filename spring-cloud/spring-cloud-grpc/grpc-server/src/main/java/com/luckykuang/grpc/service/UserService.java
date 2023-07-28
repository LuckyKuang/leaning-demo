/*
 * Copyright 2015-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.luckykuang.grpc.service;

import com.luckykuang.common.user.UserPb;
import com.luckykuang.common.user.UserReply;
import com.luckykuang.common.user.UserRequest;
import com.luckykuang.common.user.UserServiceGrpc;
import com.luckykuang.grpc.entity.TblUser;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;

/**
 * @author luckykuang
 * @date 2023/7/27 17:51
 */
@GRpcService
public class UserService extends UserServiceGrpc.UserServiceImplBase {
    @Override
    public void queryUser(UserRequest request, StreamObserver<UserReply> responseObserver) {
        UserReply.Builder userReply =  UserReply.newBuilder();
        TblUser tblUser = new TblUser(11L, "syx", "nan");
        userReply.setCode(200).setMsg("SUCCESS").setSuccess(true);
        userReply.setData(UserReply.Data.newBuilder()
                .setUserPb(UserPb.newBuilder()
                        .setId(tblUser.getId())
                        .setName(tblUser.getName())
                        .setSex(tblUser.getSex())));
        responseObserver.onNext(userReply.build());
        responseObserver.onCompleted();
        super.queryUser(request, responseObserver);
    }
}
