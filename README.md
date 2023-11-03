# leaning-demo

研究新技术、记录新想法

```shell
# 主要框架
SpringBoot 3.1.1
SpringCloud 2022.0.3
SpringCloudAlibaba 2022.0.0.0
# 注册中心
Consul 1.15.4
Nacos 2.2.1
Eureka 2.0.0
```

## 主要内容

### 一、设计模式

[1.单例模式案例](./spring-boot/spring-boot-design-pattern/singleton-pattern)

[2.责任链模式案例](./spring-boot/spring-boot-design-pattern/chain-of-responsibility-pattern)

[3.策略模式案例](./spring-boot/spring-boot-design-pattern/strategy-pattern)

[4.适配器模式案例](./spring-boot/spring-boot-design-pattern/adapter-pattern)

### 二、多线程

[基于多线程的Excel导入/导出案例](./spring-boot/spring-boot-easyexcel)

### 三、订单超时取消处理案例

[1.JDK延迟队列DelayQueue](./spring-boot/spring-boot-order-timeout/src/main/java/com/luckykuang/order/delayed)

[2.时间轮算法(netty的HashedWheelTimer)](./spring-boot/spring-boot-order-timeout/src/main/java/com/luckykuang/order/netty)

[3.定时轮询(quartz实现)](./spring-boot/spring-boot-order-timeout/src/main/java/com/luckykuang/order/quartz)

[4.RocketMQ消息队列](./spring-boot/spring-boot-order-timeout/src/main/java/com/luckykuang/order/rocketmq)

### 集成案例

[1.基于Netty的websocket-server集成案例](./spring-boot/spring-boot-netty-websocket)

[2.基于Netty的tcp-server/tcp-client集成案例](./spring-boot/spring-boot-netty-tcp)

[3.基于jakarta注解的websocket-server/websocket-client集成案例](./spring-boot/spring-boot-jakarta-websocket)

[4.MongoDB集成案例](./spring-boot/spring-boot-mongodb)

[5.串口(Serial Port)集成案例](./spring-boot/spring-boot-serial-port)

[6.RabbitMQ集成案例](./spring-boot/spring-boot-rabbitmq)

[7.基于TCP实现机器发现/UDP实现机器广播案例](./spring-boot/spring-boot-ping-ip)

### 微服务

[1.Gateway网关案例](./spring-cloud/spring-cloud-gateway)

[2.Openfeign服务调用案例](./spring-cloud/spring-cloud-openfeign)

[3.Grpc服务调用案例](./spring-cloud/spring-cloud-grpc)

[4.Sentinel限流/熔断案例](./spring-cloud/spring-cloud-alibaba-sentinel)

[5.MQTT发布/订阅案例](./spring-cloud/spring-cloud-mqtt)

### 端口维护
```text
9012	chain-of-responsibility-pattern
9005	strategy-pattern
9009	easyexcel
9023	jakarta-websocket-client
9022	jakarta-websocket-server
9020	mongodb
9021	netty-websocket
9015	rocketmq
9010	sentinel-client
9011	sentinel-server
9000	gateway-server
9003	grpc-client
9004	grpc-server
9016	mqtt-publish-a
9017	mqtt-publish-b
9002	openfeign-client
9001	openfeign-server
9006    rabbitmq
9007    serial-port
9008    ping-ip
9013    statemachine
9014    file
9024    netty-mqtt
9025    netty-mqtt web
9026    netty-udp-client
9027    netty-udp-client web
9028    netty-udp-server
```