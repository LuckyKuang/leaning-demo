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

package com.luckykuang.grpc.service.impl;

import com.luckykuang.common.user.UserReply;
import com.luckykuang.common.user.UserRequest;
import com.luckykuang.common.user.UserServiceGrpc;
import com.luckykuang.grpc.service.UserService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.Resource;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author luckykuang
 * @date 2023/7/27 17:56
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private DiscoveryClient discoveryClient;

    @Override
    public UserReply queryUser(Integer id) {
        UserReply userReply = null;
        List<ServiceInstance> server=discoveryClient.getInstances("grpc-server");
        for (ServiceInstance serviceInstance : server) {

            String hostName=serviceInstance.getHost();
            int gRpcPort=Integer.parseInt(serviceInstance.getMetadata().get("grpc-port"));

            ManagedChannel channel=ManagedChannelBuilder.forAddress(hostName,gRpcPort).usePlaintext().build();
            UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub = UserServiceGrpc.newBlockingStub(channel);

            UserRequest userRequest = UserRequest.newBuilder().setId(id).build();
            userReply = userServiceBlockingStub.queryUser(userRequest);
        }
        return userReply;
    }
}
