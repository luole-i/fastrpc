package cn.qenan.fastrpc.server.zk;

import cn.qenan.fastrpc.common.properties.FastRpcConfigurer;
import cn.qenan.fastrpc.common.properties.ServerProperties;
import cn.qenan.fastrpc.common.util.StringUtil;
import cn.qenan.fastrpc.registry.ServiceRegistry;

/**
 * 根据配置获取注册中心
 *
 * @author luolei
 * @version 1.0
 *
 * 2019/04/21
 */
public class ZkRegistryFactory {
    private static String registry = FastRpcConfigurer.getProperty(ServerProperties.REGISTRY_TYPE);

    public static ServiceRegistry getServiceRegistry() {
        if (StringUtil.isEmpty(registry)) {
            throw new NullPointerException("configuration error >>> registry type is not null");
        }
        ServiceRegistry serviceRegistry;
        switch (registry) {
            case "redis":
                serviceRegistry = null;
                break;
            case "zookeeper":
                serviceRegistry = ZkServiceRegistry.getSingletonRegistry();
                break;
            default:
                throw new NullPointerException("configuration error >>> this type of registry is not supported");
        }
        return serviceRegistry;
    }
}
