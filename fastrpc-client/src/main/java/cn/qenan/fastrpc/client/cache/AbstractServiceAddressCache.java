package cn.qenan.fastrpc.client.cache;

import cn.qenan.fastrpc.common.exception.NullAddressException;
import cn.qenan.fastrpc.common.util.CollectionsUtil;
import cn.qenan.fastrpc.registry.AddressJson;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 地址缓存抽象类，主要功能获取注册中心的缓存地址，地址json格式
 *
 * @author luolei
 * @version 1.0
 * <p>
 * 2019/04/22
 */
public abstract class AbstractServiceAddressCache implements ServiceAddressCache {
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractServiceAddressCache.class);

    public ConcurrentHashMap<String, List<String>> cache = new ConcurrentHashMap<>();

    public List<String> acquireServiceListFromCache(String serviceName, String version) {
        manageCache(serviceName);
        List<String> addresList = new ArrayList<>();
        List<String> tempList = cache.get(serviceName);
        if (CollectionsUtil.isNotEmpty(tempList)) {
            for (String address : tempList) {
                JSONObject jsonObject = JSON.parseObject(address);
                String vers = jsonObject.getString(AddressJson.VERSION);
                if (vers.equals(version)) {
                    addresList.add(address);
                }
            }
        }
        if (addresList.size() == 0) {
            throw new NullAddressException("the service: " + serviceName + " and version: " + version + " has no surviving server");
        }
        return addresList;
    }

    public abstract void manageCache(String serviceName);
}
