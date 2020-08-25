package cn.qenan.fastrpc.common.properties;

public interface ServerProperties {
    //注册中心是否开启,为1表示开启，其他值或者未设置表示未开启
    String REGISTRY_IS_START = "server.registry";

    //注册中心类型 value={redis,zookeeper}
    String REGISTRY_TYPE = "server.registry.type";

    //注册中心地址
    String REGISTRY_ADDRESS = "server.registry.address";

    //zookeeper注册中心配置，不配置取默认值
    String ZK_SESSION_TIMEOUT = "server.registry.zk.sessionTimeout";
    String ZK_CONNECTION_TIMOUT = "server.registry.zk.connectionTimeout";

    //服务端服务的地址，提供给客户端，存储到注册中心以供查找
    String SERVICE_ADDRESS = "server.service.address";

    //服务权值,用作负载均衡
    String SERVICE_WEIGHT = "server.service.weight";

    //序列化工具类型 默认protostuff
    String SERIALIZE_TYPE = "server.serialize.type";

    //是否开启监控中心
    String MONITOR_START = "server.monitor";

    //监控中心服务地址
    String MONITOR_ADDRESS = "server.monitor.address";

    //受信任ip,只有允许的ip才能访问，格式每个ip之间用;隔开，添加到监控中心可配置
    String ALLOWABLE_IP = "allowable.ip";

    //限流带宽"
    String QPS = "server.rateLimiter.qps";

    //获取令牌等待时间
    String WAIRINGTIME = "server.rateLimiter.waitingTime";
}
