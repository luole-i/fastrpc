package cn.qenan.fastrpc.client.zk;

import cn.qenan.fastrpc.common.properties.ClientProperties;
import cn.qenan.fastrpc.common.properties.FastRpcConfigurer;
import cn.qenan.fastrpc.common.util.StringUtil;
import cn.qenan.fastrpc.registry.ServiceDiscovery;

/**
 * 根据配置获取注册中心
 *
 * @author luolei
 * @version 1.0
 *
 * 2019/04/21
 */
public class ZkDiscoveryFactory {
    private static String discover = FastRpcConfigurer.getProperty(ClientProperties.REGISTRY_TYPE);

    public static ServiceDiscovery getServiceDiscovery() {
        if (StringUtil.isEmpty(discover)) {
            throw new NullPointerException("configuration error >>> registry type is not null");
        }
        ServiceDiscovery serviceDiscovery;
        switch (discover) {
            case "redis":
                serviceDiscovery = null;
                break;
            case "zookeeper":
                serviceDiscovery = ZkServiceDiscovery.getSingletonDiscovery();

                break;
            default:
                throw new NullPointerException("configuration error >>> this type of registry is not supported");
        }
        return serviceDiscovery;
    }
}
