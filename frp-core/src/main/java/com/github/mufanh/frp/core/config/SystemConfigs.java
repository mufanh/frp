package com.github.mufanh.frp.core.config;

import com.github.mufanh.frp.core.extension.buildin.loadbalance.RandomLoadBalance;
import com.google.common.base.Preconditions;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author xinquan.huangxq
 */
public enum SystemConfigs {
    /**
     * 扩展插件定义文件路径
     */
    EXTENSION_DEFINITION_FILE("frp.extension.definition.file", "./extension.definition"),

    FRONTEND_ACCESS_IDLE_TIME("frp.frontend.access.idle.time", 60),

    BACKEND_READ_IDLE_TIME("frp.backend.read.idle.time", 10),
    BACKEND_WRITE_IDLE_TIME("frp.backend.write.idle.time", 5),
    BACKEND_ACCESS_IDLE_TIME("frp.backend.access.idle.time", 3),

    /**
     * 线程池默认值
     */
    TASK_EXECUTOR_SCHEDULED_POOL_SIZE("frp.executor.scheduled.pool.size", 64),
    TASK_EXECUTOR_CORE_POOL_SIZE("frp.executor.core.pool.size", 8),
    TASK_EXECUTOR_MAX_POOL_SIZE("frp.executor.max.pool.size", 1024),
    TASK_EXECUTOR_KEEP_ALIVE("frp.executor.keepalive", 600L),

    DEFAULT_LOAD_BALANCE("frp.default.load.balance", RandomLoadBalance.class.getName()),

    BACKEND_TRY_CONNECT_TIMES("frp.backend.try.connect.times", 3),

    /**
     * NETTY参数
     */

    /**
     * netty epoll开关
     */
    NETTY_EPOLL_SWITCH("frp.netty.epoll.switch", true),
    NETTY_EPOLL_LT("frp.netty.epoll.lt", true),

    NETTY_IO_RATIO("frp.netty.io.ratio", 70),

    /**
     * ChannelOption.SO_BACKLOG对应的是tcp/ip协议listen函数中的backlog参数。函数listen(int socketfd, int backlog)
     * 用来初始化服务端可连接队列。
     * 服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接，多个客户端来的时候，服务端将不能处理的客户端连接请
     * 求放在队列中等待处理，backlog参数指定了队列的大小
     */
    TCP_SO_BACKLOG("frp.netty.tcp.so.backlog", 1024),

    /**
     * ChanneOption.SO_REUSEADDR对应于套接字选项中的SO_REUSEADDR，这个参数表示允许重复使用本地地址和端口。
     * 比如，某个服务器进程占用了TCP的80端口进行监听，此时再次监听该端口就会返回错误，使用该参数就可以解决问题，
     * 该参数允许共用该端口，这个在服务器程序中比较常使用。
     * 比如某个进程非正常退出，该程序占用的端口可能要被占用一段时间才能允许其他进程使用，而且程序死掉以后，内核一
     * 需要一定的时间才能够释放此端口，不设置SO_REUSEADDR就无法正常使用该端口
     */
    TCP_SO_REUSEADDR("frp.netty.tcp.so.reuseaddr", true),

    /**
     * ChannelOption.TCP_NODELAY参数对应于套接字选项中的TCP_NODELAY,该参数的使用与Nagle算法有关。
     * Nagle算法是将小的数据包组装为更大的帧然后进行发送，而不是输入一次发送一次，因此在数据包不足的时候会
     * 等待其他数据的到来，组装成大的数据包进行发送，虽然该算法有效提高了网络的有效负载，但是却造成了延时。
     * 而该参数的作用就是禁止使用Nagle算法，使用于小数据即时传输。和TCP_NODELAY相对应的是TCP_CORK，
     * 该选项是需要等到发送的数据量最大的时候，一次性发送数据，适用于文件传输。
     */
    TCP_NODELAY("frp.netty.tcp.nodelay", true),

    /**
     * Channeloption.SO_KEEPALIVE参数对应于套接字选项中的SO_KEEPALIVE，该参数用于设置TCP连接，
     * 当设置该选项以后，连接会测试链接的状态，这个选项用于可能长时间没有数据交流的连接。
     * 当设置该选项以后，如果在两小时内没有数据的通信时，TCP会自动发送一个活动探测数据报文。
     */
    TCP_SO_KEEPALIVE("frp.netty.tcp.so.keepalive", true),

    /**
     * ChannelOption.SO_SNDBUF参数对应于套接字选项中的SO_SNDBUF，
     * ChannelOption.SO_RCVBUF参数对应于套接字选项中的SO_RCVBUF
     * 这两个参数用于操作发送缓冲区大小和接受缓冲区大小。
     * 接收缓冲区用于保存网络协议站内收到的数据，直到应用程序读取成功，发送缓冲区用于保存发送数据，直到发送成功。
     */
    TCP_SO_SNDBUF("frp.netty.tcp.so.sndbuf", null),
    TCP_SO_RCVBUF("frp.netty.tcp.so.rcvbuf", null),

    /**
     * 高水位的时候就会可以通知到业务handler中的WritabilityChanged方法，并且修改buffer的状态，
     * channel调用isWriteable的时候就会返回false，当前channel处于不可写状态。
     * 如果低于该水位就会设置当前的channel为可写，然后触发可读事件。
     * 水位配置可以帮助我们监控缓冲区的使用情况，在写数据的时候需要判断当前channel是否可以继续向缓冲区
     * 写数据（isWriteable）。在之前的工作中出现过没有正确判断，而使用的编码器默认使用的又是堆外内存，
     * 导致在不断写入缓存的时候堆外内存超过jvm配置最大值
     */
    NETTY_BUFFER_LOW_WATERMARK("frp.netty.buffer.low.watermark", 32 * 1024),
    NETTY_BUFFER_HIGH_WATERMARK("frp.netty.buffer.high.watermark", 64 * 1024),

    NETTY_BUFFER_POOLED("frp.netty.buffer.pooled", true),
    ;

    @Getter
    private final String propertyKey;

    @Getter
    private final Object defaultValue;

    SystemConfigs(String propertyKey, Object defaultValue) {
        this.propertyKey = Preconditions.checkNotNull(propertyKey);
        this.defaultValue = defaultValue;
    }

    public boolean boolValue() {
        Preconditions.checkArgument(defaultValue instanceof Boolean);

        String propertyValue = systemPropertyValue();
        return StringUtils.isBlank(propertyValue)
                ? (boolean) defaultValue
                : Boolean.parseBoolean(propertyValue);
    }

    public Boolean getBool() {
        Preconditions.checkArgument(defaultValue == null
                || defaultValue instanceof Boolean);

        String propertyValue = systemPropertyValue();
        if (StringUtils.isBlank(propertyValue)) {
            if (defaultValue == null) {
                return null;
            }
            return (Boolean) defaultValue;
        }
        return Boolean.parseBoolean(propertyValue);
    }

    public int intValue() {
        Preconditions.checkArgument(defaultValue instanceof Integer);

        String propertyValue = systemPropertyValue();
        return StringUtils.isBlank(propertyValue)
                ? (int) defaultValue
                : Integer.parseInt(propertyValue);
    }

    public Integer getInt() {
        Preconditions.checkArgument(defaultValue == null
                || defaultValue instanceof Integer);

        String propertyValue = systemPropertyValue();
        if (StringUtils.isBlank(propertyValue)) {
            if (defaultValue == null) {
                return null;
            }
            return (Integer) defaultValue;
        }
        return Integer.parseInt(propertyValue);
    }

    public long longValue() {
        Preconditions.checkArgument(defaultValue instanceof Long);

        String propertyValue = systemPropertyValue();
        return StringUtils.isBlank(propertyValue)
                ? (long) defaultValue
                : Long.parseLong(propertyValue);
    }

    public Long getLong() {
        Preconditions.checkArgument(defaultValue == null
                || defaultValue instanceof Long);

        String propertyValue = systemPropertyValue();
        if (StringUtils.isBlank(propertyValue)) {
            if (defaultValue == null) {
                return null;
            }
            return (Long) defaultValue;
        }
        return Long.parseLong(propertyValue);
    }

    public String stringValue() {
        Preconditions.checkArgument(defaultValue == null
                || defaultValue instanceof String);

        String propertyValue = systemPropertyValue();
        return StringUtils.isBlank(propertyValue)
                ? (String) defaultValue
                : propertyValue;
    }

    private String systemPropertyValue() {
        return System.getProperty(propertyKey);
    }
}
