package cn.qenan.fastrpc.client.cache;

import cn.qenan.fastrpc.common.properties.FastRpcConfigurer;
import cn.qenan.fastrpc.common.properties.FastRpcProperties;
import cn.qenan.fastrpc.common.util.SpringContextUtil;
import cn.qenan.fastrpc.common.util.StringUtil;
import cn.qenan.fastrpc.registry.GetRegistryUtil;
import cn.qenan.fastrpc.registry.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * 地址缓存抽象类，主要功能获取注册中心的缓存地址，地址json格式
 *
 * @author luolei
 * @version 1.0
 *
 * 2019/04/22
 */
public abstract class AbstractServiceAddressCache implements ServiceAddressCache {
    protected HashMap<String, List<String>> cache = new HashMap<String, List<String>>();

    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractServiceAddressCache.class);

    public List<String> acquireServiceList(String serviceName) {
        return acquireServiceList(serviceName, "");
    }

    protected void init() {
        String apiPackage = FastRpcConfigurer.getProperty(FastRpcProperties.SERVICE_ADDRESS_CACHE_API_PACKAGE);
        if (StringUtil.isEmpty(apiPackage)) {
            throw new NullPointerException("service address cache api package is null or is not set");
        }
        ApplicationContext context = SpringContextUtil.getContext();
        GetPackageDownClass getPackageDownClass = (GetPackageDownClass) context.getBean("getPackageDownClass");
        apiPackage = StringUtil.ReplacePackageToPath(apiPackage);
        String path = "classpath:" + apiPackage + "*.class";
        List<String> serviceNameList = null;
        try {
            serviceNameList = getPackageDownClass.get(path);
        } catch (IOException e) {
            LOGGER.error("cannot this package:{}", path);
            throw new NullPointerException("cannot this package:" + path + e.getMessage());
        }
        ServiceDiscovery serviceDiscovery = GetRegistryUtil.getServiceDiscovery();
        for (String serviceName : serviceNameList) {
            if (serviceDiscovery.discoverall(serviceName) != null)
                cache.put(serviceName, serviceDiscovery.discoverall(serviceName));
        }
    }

}
