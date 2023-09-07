# Docker下安装RocketMQ-5.1.3教程

## 一、安装NameServer

NameServer是一个简单的 Topic 路由注册中心，支持 Topic、Broker 的动态注册与发现，几乎无状态节点，因此可集群部署，节点之间无任何信息同步，所谓的集群部署和单机部署其实是一样的，需要多个NameServer保持高可用只需独立部署即可。

### 1.创建挂载文件夹

```shell
# 日志目录
mkdir /home/pontus.fan/rocketmq/nameserver/logs -p
# 脚本目录
mkdir /home/pontus.fan/rocketmq/nameserver/bin -p
```

### 2.设置权限

```shell
# 777 文件所属者、文件所属组和其他人有读取 & 写入 & 执行全部权限。rwxrwxrwx
chmod 777 -R /home/pontus.fan/rocketmq/nameserver/*
```

### 3.运行nameserver容器

```shell
docker run -d \
--privileged=true \
--name rmqnamesrv \
apache/rocketmq:5.1.3 sh mqnamesrv
```

### 4.复制容器中的目录到本地磁盘

```shell
docker cp rmqnamesrv:/home/rocketmq/rocketmq-5.1.3/bin/runserver.sh /home/pontus.fan/rocketmq/nameserver/bin/runserver.sh
```

### 5.打开脚本文件【注释掉：calculate_heap_sizes】

```shell
vi /home/pontus.fan/rocketmq/nameserver/bin/runserver.sh
```

### 6.移除nameserver容器

```shell
docker rm -f rmqnamesrv
```

### 7.重新运行nameserver容器

```shell
docker run -d \
--privileged=true \
--restart=always \
--name rmqnamesrv \
-p 9876:9876  \
-v /home/pontus.fan/rocketmq/nameserver/logs:/home/rocketmq/logs \
-v /home/pontus.fan/rocketmq/nameserver/bin/runserver.sh:/home/rocketmq/rocketmq-5.1.3/bin/runserver.sh \
-e "MAX_HEAP_SIZE=256M" \
-e "HEAP_NEWSIZE=128M" \
apache/rocketmq:5.1.3 sh mqnamesrv
```

## 二、安装Broker

### 1.创建需要的挂载目录

```shell
mkdir /home/pontus.fan/rocketmq/broker/logs -p \
mkdir /home/pontus.fan/rocketmq/broker/data -p \
mkdir /home/pontus.fan/rocketmq/broker/conf -p \
mkdir /home/pontus.fan/rocketmq/broker/bin -p
```

### 2.设置权限

```shell
# 777 文件所属者、文件所属组和其他人有读取 & 写入 & 执行全部权限。rwxrwxrwx
chmod 777 -R /home/pontus.fan/rocketmq/broker/*
```

### 3.编辑配置文件

```shell
vi /home/pontus.fan/rocketmq/broker/conf/broker.conf
```

broker.conf配置如下：

```shell
# nameServer 地址多个用;隔开 默认值null
# 例：127.0.0.1:6666;127.0.0.1:8888
namesrvAddr = 192.168.1.100:9876
# 集群名称
brokerClusterName = DefaultCluster
# 节点名称
brokerName = broker-a
# broker id节点ID， 0 表示 master, 其他的正整数表示 slave，不能小于0
brokerId = 0
# Broker服务地址	String	内部使用填内网ip，如果是需要给外部使用填公网ip
brokerIP1 = 192.168.1.100
# Broker角色
brokerRole = ASYNC_MASTER
# 刷盘方式
flushDiskType = ASYNC_FLUSH
# 在每天的什么时间删除已经超过文件保留时间的 commit log，默认值04
deleteWhen = 04
# 以小时计算的文件保留时间 默认值72小时
fileReservedTime = 72
# 是否允许Broker 自动创建Topic，建议线下开启，线上关闭
autoCreateTopicEnable=true
# 是否允许Broker自动创建订阅组，建议线下开启，线上关闭
autoCreateSubscriptionGroup=true
```

### 4.运行broker容器

```shell
docker run -d \
--name rmqbroker \
--privileged=true \
apache/rocketmq:5.1.3 \
sh mqbroker
```

### 5.复制容器中的目录到本地磁盘

```shell
docker cp rmqbroker:/home/rocketmq/rocketmq-5.1.3/bin/runbroker.sh /home/pontus.fan/rocketmq/broker/bin/runbroker.sh
```

### 6.打开脚本文件【注释掉：calculate_heap_sizes】

```shell
vi /home/pontus.fan/rocketmq/broker/bin/runbroker.sh
```

### 7.移除broker容器

```shell
docker rm -f rmqbroker
```

### 8.重新运行broker容器

```shell
docker run -d \
--restart=always \
--name rmqbroker \
-p 10911:10911 \
-p 10909:10909 \
--privileged=true \
-v /home/pontus.fan/rocketmq/broker/logs:/root/logs \
-v /home/pontus.fan/rocketmq/broker/store:/root/store \
-v /home/pontus.fan/rocketmq/broker/conf/broker.conf:/home/rocketmq/broker.conf \
-v /home/pontus.fan/rocketmq/broker/bin/runbroker.sh:/home/rocketmq/rocketmq-5.1.3/bin/runbroker.sh \
-e "MAX_HEAP_SIZE=512M" \
-e "HEAP_NEWSIZE=256M" \
apache/rocketmq:5.1.3 \
sh mqbroker -c /home/rocketmq/broker.conf
```

## 三、安装rocketmq-dashboard仪表盘

### 1.拉取容器

```shell
docker pull apacherocketmq/rocketmq-dashboard:1.0.0
```

### 2.运行rocketmq-dashboard容器

```shell
docker run -d \
--restart=always \
--name rmqdashboard \
-e "JAVA_OPTS=-Xmx256M -Xms256M -Xmn128M -Drocketmq.namesrv.addr=192.168.1.100:9876 -Dcom.rocketmq.sendMessageWithVIPChannel=false" \
-p 9976:8080 \
apacherocketmq/rocketmq-dashboard:1.0.0
```

> Tips：本教程属于单机部署，仅用于测试
> 
> 部署到云服务器需要开通的端口：9876、9976、10911、10909