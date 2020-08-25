package cn.qenan.fastrpc.client.cache;

import cn.qenan.fastrpc.common.properties.ClientProperties;
import cn.qenan.fastrpc.common.properties.FastRpcConfigurer;

public class CacheFactory {
    private static AbstractServiceAddressCache cache;

    private static String isStartRegistry = FastRpcConfigurer.getProperty(ClientProperties.REGISTRY_IS_START);

    private static String isStartCache = FastRpcConfigurer.getProperty(ClientProperties.SERVICE_ADDRESS_CACHE);

    //如果注册中心开启，缓存开启，则选择默认缓存
    //如果注册中心开启，缓存未开启，选择每次直接查询注册中心，假缓存
    //如果注册中心未开启，通过直接地址缓存
    static {
        if (Integer.valueOf(isStartRegistry) == 1) {
            if (Integer.valueOf(isStartCache) == 1) {
                cache = DefaultServiceAddressCache.getSingletonCache();
            } else {
                cache = RegistryServiceAddressCache.getSingletonCache();
            }
        } else {
            cache = DirectServiceAddressCache.getSingletonCache();
        }
    }

    public static AbstractServiceAddressCache getCache() {
        return cache;
    }
}
