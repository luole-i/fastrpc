package cn.qenan.fastrpc.registry;

import cn.qenan.fastrpc.common.properties.FastRpcConfigurer;
import cn.qenan.fastrpc.common.properties.FastRpcProperties;
import cn.qenan.fastrpc.common.util.SpringContextUtil;
import cn.qenan.fastrpc.common.util.StringUtil;
import org.springframework.context.ApplicationContext;

/**
 * 根据配置获取注册中心
 *
 * @author luolei
 * @version 1.0
 *
 * 2019/04/21
 */
public class GetRegistryUtil {
    private static ApplicationContext context = SpringContextUtil.getContext();

    private static String registryType = FastRpcConfigurer.getProperty(FastRpcProperties.REGISTRY_TYPE);

    public static ServiceDiscovery getServiceDiscovery() {
        if (StringUtil.isEmpty(registryType)) {
            throw new NullPointerException("registry type is not null");
        }
        ServiceDiscovery serviceDiscovery;
        switch (registryType) {
            case "redis":
                serviceDiscovery = (ServiceDiscovery) context.getBean("redisServiceDiscovery");
                break;
            case "zookeeper":
                serviceDiscovery = (ServiceDiscovery) context.getBean("zkServiceDiscovery");
                break;
            default:
                throw new NullPointerException("this type of registry is not supported");
        }
        return serviceDiscovery;
    }

    public static ServiceRegistry getServiceRegistry() {
        if (StringUtil.isEmpty(registryType)) {
            throw new NullPointerException("registry type is not null");
        }
        ServiceRegistry serviceRegistry;
        switch (registryType) {
            case "redis":
                serviceRegistry = (ServiceRegistry) context.getBean("redisServiceRegistry");
                break;
            case "zookeeper":
                serviceRegistry = (ServiceRegistry) context.getBean("zKServiceRegistry");
                break;
            default:
                throw new NullPointerException("this type of registry is not supported");
        }
        return serviceRegistry;
    }
}
