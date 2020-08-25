package cn.qenan.fastrpc.client.cache;

import cn.qenan.fastrpc.common.properties.ClientProperties;
import cn.qenan.fastrpc.common.properties.FastRpcConfigurer;
import cn.qenan.fastrpc.common.util.CollectionsUtil;
import cn.qenan.fastrpc.common.util.PackageUtil;
import cn.qenan.fastrpc.common.util.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

/**
 * 默认缓存策略，通过将地址放到内存中的hashmap缓存起来
 *
 * @author luolei
 * @version 1.0
 * <p>
 * 2019/04/22
 */
public class DefaultServiceAddressCache extends RegistryServiceAddressCache implements ManageServiceAddressCache {
    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultServiceAddressCache.class);

    private volatile static DefaultServiceAddressCache singletonsCache;


    //初始化，根据配置看看是否需要初始化，并且将此类注册到监听器
    private DefaultServiceAddressCache() {
        String cacheInitialize = FastRpcConfigurer.getProperty(ClientProperties.SERVICE_ADDRESS_CACHE_INIT);
        if (StringUtil.isNotEmpty(cacheInitialize) && Integer.valueOf(cacheInitialize) == 1) {
            initCache();
        }
        ServiceChangeListener serviceChangeListener = ServiceChangeListener.getSingleListener();
        serviceChangeListener.register(this);
        LOGGER.debug("turn on the service that listens to the registry");
    }


    public void initCache() {
        LOGGER.debug("init client interface service address cache");
        String path = FastRpcConfigurer.getProperty(ClientProperties.SERVICE_ADDRESS_CACHE_API_PACKAGE);
        if (StringUtil.isEmpty(path)) {
            throw new NullPointerException("configuration error >>> service address cache api package is null or is not set");
        }
        List<String> serviceNameList;
        serviceNameList = PackageUtil.getClassNameList(path);
        for (String serviceName : serviceNameList) {
            List<String> list = serviceDiscovery.discover(serviceName);
            if (CollectionsUtil.isNotEmpty(list)) {
                LOGGER.debug("add service : {} to the cache", serviceName);
                cache.put(serviceName, list);
            }
        }
    }

    @Override
    public synchronized void manageCache(String serviceName) {
        if (!cache.containsKey(serviceName)) {
            cache.put(serviceName, discoverServiceAddress(serviceName));
            LOGGER.debug("add service: {} to the cache", serviceName);
        }
    }

    @Override
    public synchronized void serviceChange(String serviceName) {
        List<String> list = serviceDiscovery.discover(serviceName);
        cache.put(serviceName, list);
        LOGGER.debug("refresh the cache for the service : {}", serviceName);
    }

    @Override
    public void removeServiceAddress(String serviceName, String serviceAddress, String version) {
        List<String> list = cache.get(serviceName);
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String x = iterator.next();
            if (x.contains(serviceAddress) && x.contains(version)) {
                iterator.remove();
                LOGGER.debug("remove address:{} in service:{}", serviceAddress, serviceName);
            }
        }
    }

    public static DefaultServiceAddressCache getSingletonCache() {
        if (singletonsCache == null) {
            synchronized (DefaultServiceAddressCache.class) {
                if (singletonsCache == null) {
                    singletonsCache = new DefaultServiceAddressCache();
                }
            }
        }
        return singletonsCache;
    }

    @Override
    public List<String> acquireServiceList(String serviceName, String version) {
        return acquireServiceListFromCache(serviceName, version);
    }
}
