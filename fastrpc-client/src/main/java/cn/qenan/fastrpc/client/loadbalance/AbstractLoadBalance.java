package cn.qenan.fastrpc.client.loadbalance;

import cn.qenan.fastrpc.client.cache.AbstractServiceAddressCache;
import cn.qenan.fastrpc.client.cache.CacheFactory;
import cn.qenan.fastrpc.client.cache.ServiceAddressCache;
import cn.qenan.fastrpc.registry.AddressJson;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 负载均衡抽象类，所有负载策略类均需要继承这个类，并实现getAddressByLoadBalance和refreshStrategy方法
 *
 * @author luolei
 * @version 1.0
 */

public abstract class AbstractLoadBalance implements LoadBalance {
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractLoadBalance.class);

    protected ServiceAddressCache cache = CacheFactory.getCache();

    //用来保存从缓存里面获取的地址
    protected ConcurrentHashMap<Pair<String, String>, List<String>> backCache = new ConcurrentHashMap<>();

    public String acquireAddress(String serviceName, String version){
        Pair<String, String> pair = new Pair<>(serviceName, version);
        List<String> list = cache.acquireServiceList(serviceName, version);
        //每次从缓存里面获取最新的地址
        backCache.put(pair, list);
        String address = getOnlyAddress(getAddressByLoadBalance(pair));
        LOGGER.debug("get a service address : {}", address);
        return address;
    }

    private String getOnlyAddress(String addressJson) {
        JSONObject jsonObject = JSON.parseObject(addressJson);
        return jsonObject.getString(AddressJson.ADDRESS);
    }

    public abstract String getAddressByLoadBalance(Pair<String, String> pair);
}
