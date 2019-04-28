package cn.qenan.fastrpc.client.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认缓存策略，通过将地址放到内存中的hashmap缓存起来,单例模式
 *
 * @author luolei
 * @version 1.0
 * <p>
 * 2019/04/22
 */
public class DefaultServiceAddressCache extends AbstractServiceAddressCache {
    private DefaultServiceAddressCache() {
    }

    private volatile static ServiceAddressCache singletonsCache;

    public List<String> acquireServiceList(String serviceName, String version) {
        init();
        List<String> addresList = new ArrayList<String>();
        if (cache.containsKey(serviceName)) {
            List<String> tempList = cache.get(serviceName);
            for (int i = 0; i < tempList.size(); i++) {
                JSONObject jsonObject = JSON.parseObject(tempList.get(i));
                String vers = jsonObject.getString("version");
                if (vers.equals(version)) {
                    addresList.add(tempList.get(i));
                }
            }
        }
        return addresList;
    }

    /**
     * 单例模式，双检锁
     * @return 返回缓存单例对象
     */
    public static ServiceAddressCache getSingletonCache() {
        if (singletonsCache == null) {
            synchronized (DefaultServiceAddressCache.class) {
                if (singletonsCache == null) {
                    singletonsCache = new DefaultServiceAddressCache();
                }
            }
        }
        return singletonsCache;
    }
}
