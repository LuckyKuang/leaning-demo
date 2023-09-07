 # Netty框架
 
## TCP参数设置ChannelOption

ChannelOption参数
Bootstrap是Netty提供的客户端连接工具类，主要于简化客户端的创建。常用功能之一是TCP参数设置接口

```text
1、SO_TIMEOUT
控制读取操作将阻塞多少毫秒，如果返回值为0，计时器就被禁止了，该线程将被无限期阻塞。

2、SO_SNDBUF
套接字使用的发送缓冲区大小

3、SO_RCVBUF
套接字使用的接收缓冲区大小

4、SO_REUSEADDR
是否允许重用端口。默认值与系统相关。

5、CONNECT_TIMEOUT_MILLIS
客户端连接超时时间，原生NIO不提供该功能，Netty使用的是自定义连接超时定时器检测和超时控制

6、TCP_NODELAY
是否使用Nagle算法，设置为true关闭Nagle算法。
如果是时延敏感型的应用，建议关闭。
Nagle算法将小的碎片数据连接成更大的报文来最小化所发送的报文的数量。

7、SO_KEEPALIVE
是否使用TCP的心跳机制。
当设置该选项以后，如果在两小时内没有数据的通信时，TCP会自动发送一个活动探测数据报文。
建议：心跳机制由应用层自己实现；
```

AbstractBootstrap类源码

```text
private final Map<ChannelOption<?>, Object> options = new LinkedHashMap();
   
/**
 * Channel实例被创建时，指定使用的ChannelOption参数。
 * value参数为null，则移除这个ChannelOption参数
 * 
 * Allow to specify a {@link ChannelOption} which is used for the {@link Channel}
 * instances once they got created.
 * Use a value of {@code null} to remove a previous set {@link ChannelOption}.
 */
public <T> B option(ChannelOption<T> option, T value) {
    //判空
    if (option == null) {
        throw new NullPointerException("option");
    } else {
        if (value == null) {
            //从map中移除
            synchronized(this.options) {
                this.options.remove(option);
            }
        } else {
            //添加到map
            synchronized(this.options) {
                this.options.put(option, value);
            }
        }

        return this.self();
    }
}
```

案例：

```text
EventLoopGroup group = new NioEventLoopGroup();
Bootstrap b = new Bootstrap();
b.group(group)
 .channel(NioSocketChannel.class)
 .option(ChannelOption.TCP_NODELAY, true)
 .handler(new ChannelInitializer<SocketChannel>() {
     @Override
     public void initChannel(SocketChannel ch) throws Exception {
         ChannelPipeline p = ch.pipeline();
         p.addLast(new LoggingHandler(LogLevel.INFO));
     }
 });
ChannelFuture f = b.connect(HOST, PORT).sync();
```