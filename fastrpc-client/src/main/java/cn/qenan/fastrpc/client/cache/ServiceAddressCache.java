package cn.qenan.fastrpc.client.cache;

import java.util.List;

public interface ServiceAddressCache {
    List<String> acquireServiceList(String serviceName);

    List<String> acquireServiceList(String serviceName, String version);
}
