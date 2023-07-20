# Sentinel笔记

> 本教程以`sentinel-dashboard-1.8.6.jar`为例，`nacos`版本为`2.2.1`

## 一、获取控制台

Sentinel控制台jar包：[Releases · alibaba/Sentinel · GitHub](https://github.com/alibaba/Sentinel)

Sentinel控制台源码：[GitHub - alibaba/Sentinel](https://github.com/alibaba/Sentinel)

## 二、启动控制台

Linux下后台静默启动方式如下：

```shell
nohup java -server -Xms256m -Xmx256m -Dserver.port=8080 -Dcsp.sentinel.dashboard.server=localhost:8080 -Dproject.name=sentinel-dashboard -jar --add-exports=java.base/sun.net.util=ALL-UNNAMED sentinel-dashboard-1.8.6.jar &
```

Windows不想后台静默启动，将首尾的`nohup`和`&`去掉即可

具体的启动参数介绍：

- `-Dserver.port=8080` 控制台端口，sentinel控制台是一个spring boot程序。客户端配置文件需要填对应的配置，如：spring.cloud.sentinel.transport.dashboard=localhost:8080
- `-Dcsp.sentinel.dashboard.server=localhost:8080` 控制台的地址，指定控制台后客户端会自动向该地址发送心跳包。
- `-Dproject.name=sentinel-dashboard` 指定Sentinel控制台程序的名称
- `-Dcsp.sentinel.api.port=8719` 可选项，客户端提供给Dashboard访问或者查看Sentinel的运行访问的参数，默认8719，如果冲突会默认递增
- `--add-exports=java.base/sun.net.util=ALL-UNNAMED` 如果是`jdk17`及以上版本，必须在启动参数上添加

## 三、访问控制台

浏览器输入网址：`http://127.0.0.1:8080/#/login`

默认登陆账户：`sentinel`/`sentinel`

## 四、微服务集成

### 需要引入的依赖如下：

```text

<!-- openfeign 用于远程调用 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>

<!-- loadbalancer 用于负载均衡 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>

<!-- sentinel 用于限流/熔断 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
</dependency>

<!-- sentinel持久化到nacos 不持久每次重启都会清除规则 -->
<!-- 2022.0.0.0-RC2版本持久化无法使用，等后续更新 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-alibaba-sentinel-datasource</artifactId>
</dependency>

<!-- nacos 配置中心 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>

<!-- nacos 注册中心 -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```

### 限流操作

在`controller`类的方法接口上添加`@SentinelResource`注解

关于@SentinelResource 注解，有以下的属性：

- `value`：资源名称，必需项（不能为空）
- `entryType`：entry类型，可选项（默认为 EntryType.OUT）
- `blockHandler/blockHandlerClass`: blockHandler 对应处理 BlockException 的函数名称，可选项
- `fallback/fallbackClass`：fallback 函数名称，可选项，用于在抛出异常的时候提供 fallback 处理逻辑。 

更多使用可参考官方文档：[注解支持 · alibaba/Sentinel Wiki · GitHub](https://github.com/alibaba/Sentinel/wiki/%E6%B3%A8%E8%A7%A3%E6%94%AF%E6%8C%81)

具体代码如下：

```java
package com.luckykuang.sentinel.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.luckykuang.sentinel.feign.UserClient;
import com.luckykuang.sentinel.handler.UserHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luckykuang
 * @date 2023/7/17 11:24
 */
@Slf4j
@RestController
@RequestMapping("api/v1/user")
public class UserController {

    @Resource
    private UserClient userClient;

    @GetMapping("getUser")
    @SentinelResource(value = "getUser",blockHandler = "getUserBlockHandler")
    public String getUser(String name){
        if(name.contains("y")){
            log.info("client getUser exception name:{}",name);
            throw new RuntimeException("client getUser exception");
        }
        log.info("client getUser name:{}",name);
        return userClient.getUser(name);
    }
    /**
     * 限流/熔断降级：根据sentinel配置的阈值，超过即触发
     */
    public String getUserBlockHandler(String name, BlockException blockException){
        log.warn("限流/熔断业务处理"+"-"+name,blockException);
        return "限流/熔断业务处理"+"-"+name;
    }
}
```

### 熔断操作

具体代码如下：

```java
package com.luckykuang.sentinel.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.luckykuang.sentinel.feign.UserClient;
import com.luckykuang.sentinel.handler.UserHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luckykuang
 * @date 2023/7/17 11:24
 */
@Slf4j
@RestController
@RequestMapping("api/v1/user")
public class UserController {

    @Resource
    private UserClient userClient;

    @GetMapping("getUser")
    @SentinelResource(value = "getUser",fallback = "getUserHandlerFallback")
    public String getUser(String name){
        if(name.contains("y")){
            log.info("client getUser exception name:{}",name);
            throw new RuntimeException("client getUser exception");
        }
        log.info("client getUser name:{}",name);
        return userClient.getUser(name);
    }
    /**
     * 异常处理：接口调用异常即触发
     */
    public String getUserHandlerFallback(String name,Throwable throwable){
        log.error("异常业务处理"+"-"+name,throwable);
        return "异常业务处理"+"-"+name;
    }
}
```

### 限流/熔断

限流和熔断都写，优化后的代码如下：

控制层代码`UserController.java`

```java
package com.luckykuang.sentinel.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.luckykuang.sentinel.feign.UserClient;
import com.luckykuang.sentinel.handler.UserHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luckykuang
 * @date 2023/7/17 11:24
 */
@Slf4j
@RestController
@RequestMapping("api/v1/user")
public class UserController {

    @Resource
    private UserClient userClient;

    @GetMapping("getUser")
    @SentinelResource(value = "getUser",
            blockHandler = "getUserBlockHandler",blockHandlerClass = UserHandler.class,
            fallback = "getUserHandlerFallback",fallbackClass = UserHandler.class)
    public String getUser(String name){
        if(name.contains("y")){
            log.info("client getUser exception name:{}",name);
            throw new RuntimeException("client getUser exception");
        }
        log.info("client getUser name:{}",name);
        return userClient.getUser(name);
    }
}
```

抽离代码`UserHandler.java`

```java
package com.luckykuang.sentinel.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author luckykuang
 * @date 2023/7/17 18:14
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserHandler {

    /**
     * 限流/熔断降级：根据sentinel配置的阈值，超过即触发
     */
    public static String getUserBlockHandler(String name, BlockException blockException){
        log.warn("限流/熔断业务处理"+"-"+name,blockException);
        return "限流/熔断业务处理"+"-"+name;
    }

    /**
     * 异常处理：接口调用异常即触发
     */
    public static String getUserHandlerFallback(String name,Throwable throwable){
        log.error("异常业务处理"+"-"+name,throwable);
        return "异常业务处理"+"-"+name;
    }
}
```

## 五、测试

通过`Postman`工具，测试接口

### 1、限流测试

设置限流阈值：

参数说明：

- `resource`：资源名，即限流规则的作用对象
- `limitApp`：流控针对的调用来源，若为default 则不区分调用来源
- `grade`：限流阈值类型（QPS 或并发线程数）；0代表根据并发数量来限流，1代表根据QPS来进行流量控制
- `count`：限流阈值
- `strategy`：调用关系限流策略（0代表直接、1代表关联、2代表链路）
- `controlBehavior`：流量控制效果（0代表直接拒绝、1代表Warm Up、2代表匀速排队）
- `clusterMode`：是否为集群模式 true-是 false-否

请求接口：`http://localhost:9010/api/v1/user/getUser?name=x`

**测试结果**：
- 规则配置在`/api/v1/user/getUser`接口上，请求超过设定的阈值，就会返回`Blocked by Sentinel (flow limiting)`
- 规则配置在`getUser`接口上，请求超过设定的阈值，就会返回`限流/熔断业务处理-x`，进入限流处理业务，这里的`getUser`是注解`@SentinelResource`上的`value`值，该值一定要唯一

### 2、熔断测试

慢调用比例配置：

参数说明：

- `最大RT`：即接口的最大响应时间，单位：毫秒，当接口实际响应时间超过该设定的值时，sentinel会认为此次请求是一个慢调用请求
- `比例阈值`：取值范围0.1~1.0，在统计时长区间内，当sentinel统计到的慢调用次数占总的调用次数的比例，达到此阈值时，触发熔断。
- `熔断时长`：单位(秒)，首次触发熔断，多长时间后再次尝试请求服务。如果首次熔断后，没有达到设定的熔断时长，再次请求时，不会直接调用服务，而是直接抛出异常，或者走blockHandler指定的异常处理逻辑。如果达到设定的熔断时长之后，再次请求服务时，如果sentinel发现请求耗时还是无法满足最大RT设定值时，会继续熔断，直到请求耗时满足最大RT设定值时恢复正常。
- `最小请求数`：统计时长区间内，需要采集的最小请求样本数，如果统计时长区间内，所有请求都达到了最大RT，但是总的请求数没有达到设定的最小请求数，那么也不会触发熔断
- `统计时长`：单位(毫秒)

**测试结果**：
- 达到慢比例熔断策略，也就是接口慢请求达到设置比例时，即会触发`blockHandler`处理

异常比例配置：

参数说明：

- `比例阈值`：取值区间0.1~1.0，统计时长区间内，如果发生异常的请求数占总的请求数的比例达到该阈值后，sentinel会触发熔断机制

**测试结果**：
- 达到异常比例熔断策略，也就是接口请求出现异常比例达到设置比例时，即会触发`blockHandler`处理

异常数配置：

参数说明：

- `异常数`：统计时长区间内，如果发生异常的请求数达到该阈值时，sentinel触发熔断机制

**测试结果**：
- 达到异常数熔断策略，也就是接口请求出现异常次数达到设置异常数量时，即会触发`blockHandler`处理









