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

package com.luckykuang.netty.util;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * websocket工具类
 * ChannelGroup是netty提供用于管理web于服务器建立的通道channel的，其本质是一个高度封装的set集合。
 * 在服务器广播消息时，可以直接通过它的writeAndFlush将消息发送给集合中的所有通道中去。
 * 但在查找某一个客户端的通道时候，必须通过channelId对象去查找，而channelId不能人为创建，所有必须通过map将channelId的字符串和channel保存起来。
 * @author luckykuang
 * @date 2023/8/21 17:46
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WebsocketChannelUtils {

    // 管道组
    private static final ChannelGroup GlobalGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    // 缓存管道id
    private static final ConcurrentMap<String, ChannelId> ChannelMap = new ConcurrentHashMap<>();
    // 客户端数量统计
    public static final AtomicInteger ClientConnect = new AtomicInteger(0);

    /**
     * 添加管道
     */
    public static void addChannel(Channel channel) {
        GlobalGroup.add(channel);
        ChannelMap.put(channel.id().asLongText(), channel.id());
        ClientConnect.getAndIncrement();
        log.info("当前已连接客户端数量：{}",ClientConnect.get());
    }

    /**
     * 删除管道
     */
    public static void removeChannel(Channel channel) {
        GlobalGroup.remove(channel);
        ChannelMap.remove(channel.id().asLongText());
        ClientConnect.getAndDecrement();
        log.info("当前已连接客户端数量：{}",ClientConnect.get());
    }

    /**
     * 根据管道id查询管道
     * @param id 客户端管道id
     */
    public static Channel findChannel(String id) {
        return GlobalGroup.find(ChannelMap.get(id));
    }

    /**
     * 查询所有管道
     */
    public static List<Channel> findAllChannel() {
        List<Channel> channels = new ArrayList<>();
        ChannelMap.forEach((key,value) -> {
            Channel channel = GlobalGroup.find(ChannelMap.get(key));
            channels.add(channel);
        });
        return channels;
    }

    /**
     * 服务端向指定客户端发送消息
     * @param id 客户端管道id
     * @param tws 需要发送的消息
     */
    public static void sendMsgById(String id, TextWebSocketFrame tws) {
        ChannelId channelId = ChannelMap.get(id);
        if (channelId == null){
            return;
        }
        Channel channel = GlobalGroup.find(channelId);
        channel.writeAndFlush(tws);
    }

    /**
     * 服务端向所有客户端发送消息
     * @param tws 需要发送的消息
     */
    public static void sendMsgToAll(TextWebSocketFrame tws) {
        GlobalGroup.writeAndFlush(tws);
    }
}
