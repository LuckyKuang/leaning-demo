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

[1.单例模式案例](./spring-boot/design-pattern/singleton-pattern)

[2.责任链模式案例](./spring-boot/design-pattern/chain-of-responsibility-pattern)

[3.策略模式案例](./spring-boot/design-pattern/strategy-pattern)

### 二、多线程

[基于多线程的Excel导入案例](./spring-boot/multi-thread)

### 三、订单超时取消处理案例

[1.JDK延迟队列DelayQueue](./spring-boot/order-timeout-scheme/src/main/java/com/luckykuang/order/delayed)

[2.时间轮算法(netty的HashedWheelTimer)](./spring-boot/order-timeout-scheme/src/main/java/com/luckykuang/order/netty)

[3.定时轮询(quartz实现)](./spring-boot/order-timeout-scheme/src/main/java/com/luckykuang/order/quartz)

[4.RocketMQ消息队列](./spring-boot/order-timeout-scheme/src/main/java/com/luckykuang/order/rocketmq)

### 四、微服务

[1.Gateway网关案例](./spring-cloud/spring-cloud-gateway)

[2.Openfeign服务调用案例](./spring-cloud/spring-cloud-openfeign)

[3.Grpc服务调用案例](./spring-cloud/spring-cloud-grpc)

[4.Sentinel限流/熔断案例](./spring-cloud/spring-cloud-alibaba-sentinel)

[5.MQTT发布/订阅案例](./spring-cloud/spring-cloud-mqtt)