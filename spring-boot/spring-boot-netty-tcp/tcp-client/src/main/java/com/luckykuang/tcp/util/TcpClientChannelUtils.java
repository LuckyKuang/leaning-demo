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

package com.luckykuang.tcp.util;

import io.netty.channel.Channel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * tcp工具类
 * ChannelGroup是netty提供用于管理web于服务器建立的通道channel的，其本质是一个高度封装的set集合。
 * 在服务器广播消息时，可以直接通过它的writeAndFlush将消息发送给集合中的所有通道中去。
 * 但在查找某一个客户端的通道时候，必须通过channelId对象去查找，而channelId不能人为创建，所有必须通过map将channelId的字符串和channel保存起来。
 * @author luckykuang
 * @date 2023/8/21 17:46
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TcpClientChannelUtils {

    // 管道组
//    private static final ChannelGroup GlobalGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    // 缓存管道id
    private static final ConcurrentMap<String, Channel> channelMap = new ConcurrentHashMap<>();

    /**
     * 添加管道
     */
    public static void addChannel(Channel channel) {
//        GlobalGroup.add(channel);
        channelMap.put(channel.id().asShortText(), channel);
    }

    /**
     * 删除管道
     */
    public static void removeChannel(Channel channel) {
//        GlobalGroup.remove(channel);
        channelMap.remove(channel.id().asShortText());
        channel.close();
    }

    /**
     * 获取管道
     */
    public static Optional<Channel> getChannel(String channelId){
        return Optional.ofNullable(channelMap.get(channelId));
    }

    /**
     * 根据管道id查询管道
     * @param channelId 客户端管道id
     */
//    public static Channel findChannel(String channelId) {
//        return GlobalGroup.find(channelMap.get(channelId).id());
//    }
}
