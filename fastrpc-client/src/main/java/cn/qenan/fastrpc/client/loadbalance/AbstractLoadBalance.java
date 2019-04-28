package cn.qenan.fastrpc.client.loadbalance;

import cn.qenan.fastrpc.client.cache.DefaultServiceAddressCache;
import cn.qenan.fastrpc.client.cache.ServiceAddressCache;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalance {
    protected ServiceAddressCache cache = DefaultServiceAddressCache.getSingletonCache();

    private HashMap<Pair<String, String>, List<String>> hashMap = new HashMap<>();

    public String acquireAddress(String serviceName) throws NullPointerException {
        return acquireAddress(serviceName, "");
    }

    public String acquireAddress(String serviceName, String version) throws NullPointerException {
        Pair<String, String> pair = new Pair<>(serviceName, version);
        if (hashMap.containsKey(pair)) {
           return getAddressByLoadBlance(pair);
        }
        List<String> list = cache.acquireServiceList(serviceName, version);
        hashMap.put(pair,list);
        return getAddressByLoadBlance(pair);
    }

     abstract String getAddressByLoadBlance(Pair<String,String> pair);
}
