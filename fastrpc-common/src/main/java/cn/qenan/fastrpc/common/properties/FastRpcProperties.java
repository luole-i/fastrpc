package cn.qenan.fastrpc.common.properties;

/**
 * 配置文件列表
 *
 * @author luolei
 * @version 1.0
 * <p>
 * 2019/04/19
 */
public interface FastRpcProperties {

    //**********************************registry*************************************
    /**
     * 表示注册中心是否开启
     */
    String REGISTRY_IS_START = "registry";
    /**
     * 注册中心类型 value={redis,zookeeper}
     */
    String REGISTRY_TYPE = "registry.type";
    /**
     * 注册中心地址
     */
    String REGISTRY_ADDRESS = "registry.address";
    /**
     * zookeeper注册中心配置，不配置取默认值
     */
    String ZK_SESSION_TIMEOUT = "registry.zk.session.timeout";
    String ZK_CONNECTION_TIMOUT = "registry.zk.connection.timeout";

    //**********************************common*************************************


    //**********************************server*************************************

    /**
     * 服务端服务的地址，提供给客户端，存储到注册中心以供查找
     */
    String SERVICE_ADDRESS = "server.service.address";
    /**
     * 服务地址权值
     */
    String SERVICE_WEIGHT = "server.service.weight";
    /**
     * commmon
     */

    /**
     * 序列化工具类型 默认protostuff
     */
    String SERIALIZE_TYPE = "serialize.type";

    //**********************************client*************************************

    /**
     * 表示客户端服务地址是否缓存
     * value=1 表示开启 value=X或者没有这个配置表示不开启
     * 推荐开启
     */
    String SERVICE_ADDRESS_CACHE = "client.address.cache";
    /**
     * 开启缓存时要查询的服务接口的包地址
     */
    String SERVICE_ADDRESS_CACHE_API_PACKAGE = "client.address.cache.api.package";
    /**
     * 缓存类型
     */
    String SERVICE_ADDRESS_CACHE_TYPE = "client.addres.cache.type";
}
