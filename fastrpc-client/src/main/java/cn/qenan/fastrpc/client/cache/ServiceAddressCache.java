package cn.qenan.fastrpc.client.cache;

import java.util.List;

/**
 * 服务地址缓存接口，通过获取服务地址方法
 */
public interface ServiceAddressCache {
    List<String> acquireServiceList(String serviceName, String version);
}
