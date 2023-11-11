# leaning-demo

> 研究新技术、记录新想法

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

- [1.单例模式案例](./spring-boot/spring-boot-design-pattern/singleton-pattern)

- [2.责任链模式案例](./spring-boot/spring-boot-design-pattern/chain-of-responsibility-pattern)

- [3.策略模式案例](./spring-boot/spring-boot-design-pattern/strategy-pattern)

- [4.适配器模式案例](./spring-boot/spring-boot-design-pattern/adapter-pattern)

### 二、互联网相关

#### 不同方式实现订单超时案例

- [1.JDK延迟队列DelayQueue](./spring-boot/spring-boot-order-timeout/src/main/java/com/luckykuang/order/delayed)

- [2.时间轮算法(netty的HashedWheelTimer)](./spring-boot/spring-boot-order-timeout/src/main/java/com/luckykuang/order/netty)

- [3.定时轮询(quartz实现)](./spring-boot/spring-boot-order-timeout/src/main/java/com/luckykuang/order/quartz)

#### 基于EasyExcel实现Excel导入导出案例

- [基于多线程的Excel导入/导出案例](./spring-boot/spring-boot-easyexcel)

#### 基于spring状态机实现订单状态的自动切换案例

- [基于spring状态机实现订单状态的自动切换案例](./spring-boot/spring-boot-statemachine)

#### 关于文件的零拷贝实现(有测试用例)

- [1.二进制文件/普通文件的上传案例](./spring-boot/spring-boot-file)

- [2.图片与Base64互转测试用例](./spring-boot/spring-boot-file/src/test/java/com/luckykuang/file/util/FileConvertUtilsTest.java)

- [3.文件零拷贝测试用例](./spring-boot/spring-boot-file/src/test/java/com/luckykuang/file/util/FileCopyUtilsTest.java)

- [4.文件读写测试用例(推荐用字节读写)](./spring-boot/spring-boot-file/src/test/java/com/luckykuang/file/util/FileWriterUtilsTest.java)


### 三、物联网相关

#### Netty案例

- [1.基于Netty的websocket-server集成案例](./spring-boot/spring-boot-netty-websocket)

- [2.基于Netty的mqtt-server集成案例](./spring-boot/spring-boot-netty-mqtt)

- [3.基于Netty的tcp-server/tcp-client集成案例](./spring-boot/spring-boot-netty-tcp)

- [4.基于Netty的udp-server/udp-client集成案例](./spring-boot/spring-boot-netty-udp)

#### 消息队列案例

- [1.RabbitMQ集成案例](./spring-boot/spring-boot-rabbitmq)

- [2.RocketMQ集成案例](./spring-boot/spring-boot-rocketmq)

- [3.支持多消费者RocketMQ集成案例](./spring-boot/spring-boot-multi-rocketmq)

#### 数据库案例

- [1.MongoDB集成案例](./spring-boot/spring-boot-mongodb)

#### 对接硬件相关案例

- [1.基于jakarta注解的websocket-server/websocket-client集成案例](./spring-boot/spring-boot-jakarta-websocket)

- [2.基于jSerialComm实现串口通信集成案例](./spring-boot/spring-boot-serial-port)

- [3.基于TCP实现机器主动发现/基于UDP广播实现被动机器发现案例](./spring-boot/spring-boot-ping-ip)

### 四、微服务相关

- [1.Gateway网关案例](./spring-cloud/spring-cloud-gateway)

- [2.Openfeign服务调用案例](./spring-cloud/spring-cloud-openfeign)

- [3.Grpc服务调用案例](./spring-cloud/spring-cloud-grpc)

- [4.Sentinel限流/熔断案例](./spring-cloud/spring-cloud-alibaba-sentinel)

- [5.MQTT发布/订阅案例](./spring-cloud/spring-cloud-mqtt)

## 本项目启动步骤

1. 必须安装jdk17+版本，因为代码中用到了新特性
2. 推荐使用IntelliJ IDEA工具，因为简单易用
3. Maven用IntelliJ IDEA自带的就行，它会根据你的项目自动适配最合适的版本
4. File -> Project Structure -> Project,在这里将SDK选择17，再将Language level选择17，最后点击OK
5. File -> Setting -> Build,Execution,Deployment -> Build Tools -> Maven -> Importing,在这里将JDK for importer设置为17，最后点击OK
6. File -> Setting -> Build,Execution,Deployment -> Build Tools -> Maven -> Runner,在这里将JRE设置为17，最后点击OK
7. 到这里设置一切就绪，最后只需要刷新一下Maven后，等着将依赖下载完成，项目即可正常运行

注意：Maven千万别修改成国内镜像，能科学就科学，不能科学就乖乖等。很多依赖国内镜像没有，别自找麻烦哈

## 端口维护
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
