package cn.qenan.fastrpc.client.cache;

import cn.qenan.fastrpc.client.zk.ZkDiscoveryFactory;
import cn.qenan.fastrpc.common.exception.NullAddressException;
import cn.qenan.fastrpc.common.util.CollectionsUtil;
import cn.qenan.fastrpc.registry.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 直接从注册中心获取地址，不开启缓存
 */

public class RegistryServiceAddressCache extends AbstractServiceAddressCache {
    private final static Logger LOGGER = LoggerFactory.getLogger(RegistryServiceAddressCache.class);

    private volatile static RegistryServiceAddressCache singletonsCache;

    protected ServiceDiscovery serviceDiscovery = ZkDiscoveryFactory.getServiceDiscovery();

    protected List<String> discoverServiceAddress(String serviceName) {
        List<String> list = serviceDiscovery.discover(serviceName);
        if (CollectionsUtil.isEmpty(list)) {
            throw new NullAddressException("the service: " + serviceName + " has no surviving server");
        }
        return list;
    }

    @Override
    public List<String> acquireServiceList(String serviceName, String version){
        return discoverServiceAddress(serviceName);
    }

    @Override
    public void manageCache(String serviceName) {
    }

    public static RegistryServiceAddressCache getSingletonCache() {
        if (singletonsCache == null) {
            synchronized (DefaultServiceAddressCache.class) {
                if (singletonsCache == null) {
                    singletonsCache = new RegistryServiceAddressCache();
                }
            }
        }
        return singletonsCache;
    }
}
