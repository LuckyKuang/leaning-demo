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

package com.luckykuang.rocketmq.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.DefaultRocketMQListenerContainer;
import org.apache.rocketmq.spring.support.RocketMQMessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * RocketMq 配置
 * @author luckykuang
 * @date 2023/10/22 22:30
 */
@Configuration
@RequiredArgsConstructor
public class RocketMqConfig implements BeanPostProcessor {

    private final RocketMqProperties rocketMqProperties;

    @Bean
    public RocketMQTemplate rocketMQTemplate(){
        return new RocketMQTemplate();
    }

    /**
     * 解决RocketMQ Jackson不支持Java时间类型配置
     * 源码参考：{@link org.apache.rocketmq.spring.autoconfigure.MessageConverterConfiguration}
     * 在使用Java8后经常会使用LocalDate/LocalDateTime这两个时间类型字段，然而RocketMQ原始配置并不支持Java时间类型
     * 原因：RocketMQ内置使用的转换器是RocketMQMessageConverter，转换Json时使用的是MappingJackson2MessageConverter，但是这个转换器不支持时间类型。
     * 解决办法：需要自定义消息转换器，将MappingJackson2MessageConverter进行替换，并添加支持时间模块
     */
    @Bean
    @Primary
    public RocketMQMessageConverter enhanceRocketMQMessageConverter(){
        RocketMQMessageConverter converter = new RocketMQMessageConverter();
        CompositeMessageConverter compositeMessageConverter = (CompositeMessageConverter) converter.getMessageConverter();
        List<MessageConverter> messageConverters = compositeMessageConverter.getConverters();
        for (MessageConverter messageConverter : messageConverters) {
            if(messageConverter instanceof MappingJackson2MessageConverter jackson2MessageConverter){
                ObjectMapper objectMapper = jackson2MessageConverter.getObjectMapper();
                objectMapper.registerModules(new JavaTimeModule());
            }
        }
        return converter;
    }

    /**
     * 在装载Bean之前实现参数修改
     * 在使用RocketMQ时，通常会在代码中直接指定消息主题(topic)，而且开发环境和测试环境可能共用一个RocketMQ环境。
     * 如果没有进行处理，在开发环境发送的消息就可能被测试环境的消费者消费，测试环境发送的消息也可能被开发环境的消费者消费，从而导致数据混乱的问题。
     * 为了解决这个问题，我们可以根据不同的环境实现自动隔离。通过简单配置一个选项，如dev、test、prod等不同环境，所有的消息都会被自动隔离。
     * 例如，当发送的消息主题为consumer_topic时，可以自动在topic后面加上环境后缀，如consumer_topic_dev。
     * 编写一个配置类实现BeanPostProcessor，并重写postProcessBeforeInitialization方法，在监听器实例初始化前修改对应的topic。
     * BeanPostProcessor是Spring框架中的一个接口，它的作用是在Spring容器实例化、配置完bean之后，在bean初始化前后进行一些额外的处理工作。
     * BeanPostProcessor接口定义了两个方法：
     *      postProcessBeforeInitialization(Object bean, String beanName): 在bean初始化之前进行处理，可以对bean做一些修改等操作。
     *      postProcessAfterInitialization(Object bean, String beanName): 在bean初始化之后进行处理，可以进行一些清理或者其他操作。
     * BeanPostProcessor可以在应用程序中对Bean的创建和初始化过程进行拦截和修改，对Bean的生命周期进行干预和操作。
     * 它可以对所有的Bean类实例进行增强处理，使得开发人员可以在Bean初始化前后自定义一些操作，从而实现自己的业务需求。
     * 比如，可以通过BeanPostProcessor来实现注入某些必要的属性值、加入某一个对象等等。
     *          参见：{@link BeanPostProcessor}
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof DefaultRocketMQListenerContainer container){
            // 拼接Topic，用于 RocketMQ 环境隔离
            if(rocketMqProperties.isEnabledIsolation() && StringUtils.hasText(rocketMqProperties.getEnvironment())){
                container.setTopic(String.join("_", container.getTopic(), rocketMqProperties.getEnvironment()));
            }
            return container;
        }
        return bean;
    }
}
