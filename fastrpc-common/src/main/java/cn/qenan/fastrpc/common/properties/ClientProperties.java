package cn.qenan.fastrpc.common.properties;

public interface ClientProperties {
    //注册中心是否开启,为1表示开启，其他值或没有配置表示不开启
    String REGISTRY_IS_START = "client.registry";

    //表示未开启注册中心时直接的服务地址，不同的服务用;隔开，不同地址间用-隔开，服务和地址之间$隔开
    //例:Hello:1.0$127.0.0.1:8080-127.0.0.1:8000;Word$127.0.0.1:8080-127.0.0.1:8000
    //总地址，这个地址上含有所有的服务，例:127.0.0.1:8080;127.0.0.1:8000
    String SERVICE_ADDRESS = "client.directAddress";

    //注册中心类型 value={redis,zookeeper}
    String REGISTRY_TYPE = "client.registry.type";

    //注册中心地址
    String REGISTRY_ADDRESS = "client.registry.address";

    //zookeeper注册中心配置，不配置取默认值
    String ZK_SESSION_TIMEOUT = "client.registry.zk.sessionTimeout";
    String ZK_CONNECTION_TIMOUT = "client.registry.zk.connectionTimeout";

    //表示客户端服务地址是否缓存
    //value=1 表示开启 value=X或者没有这个配置表示不开启
    //推荐开启
    String SERVICE_ADDRESS_CACHE = "client.address.cache";

    //表示缓存是否需要根据接口api初始化,为1表示需要初始化，其他值或未设置不初始化
    String SERVICE_ADDRESS_CACHE_INIT = "client.address.cache.initialize";

    //开启缓存时要查询的服务接口的包地址
    String SERVICE_ADDRESS_CACHE_API_PACKAGE = "client.address.cache.apiPackage";

    //均衡负载算法,默认轮询算法
    String SERVICE_ADDRESS_LOADBALABCE = "client.address.loadBalance";

    //序列化工具类型 默认protostuff
    String SERIALIZE_TYPE = "client.serialize.type";

    //是否开启失败策略，开启失败策略遇到异常不会让项目停下来，自是将错误写进日志，并且写入监控中心
    String SERVICE_FAIL_STRATEGY_START = "client.failureStrategy";

    //重试策略类型
    String SERVICE_RETRY_STRATEGY = "client.retryStrategy";

    //重试时间,单位秒
    String SERVICE_RETRY_STRATEGY_TIME = "client.retryStrategy.time";

    //是否开启监控中心
    String MONITOR_START = "client.monitor";

    //监控中心服务地址
    String MONITOR_ADDRESS = "client.monitor.address";
}
