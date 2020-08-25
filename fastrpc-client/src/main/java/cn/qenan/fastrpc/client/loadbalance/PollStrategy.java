package cn.qenan.fastrpc.client.loadbalance;

import cn.qenan.fastrpc.common.util.CollectionsUtil;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;

/**
 * 轮询获取服务地址策略
 *
 * @author luolei
 * @version 1.0
 * <p>
 * 2019/4/27
 */
public class PollStrategy extends AbstractLoadBalance {
    protected HashMap<Pair<String, String>, List<String>> pollcache = new HashMap<>();

    @SuppressWarnings("unchecked")
    protected synchronized void initCache(Pair<String, String> pair) {
        if (!pollcache.containsKey(pair)) {
            pollcache.put(pair, (List<String>) CollectionsUtil.copyArrayList(backCache.get(pair)));
        }
    }

    @Override
    public synchronized String getAddressByLoadBalance(Pair<String, String> pair) {
        //判断轮询缓存的地址是否全部在备份缓存里，如果不是则说明发生了宕机，清除轮询缓存，重新获取最新地址再进行轮询算法
        //可改进：通过对比列表长度，测试加锁影响
        List<String> backAddress = backCache.get(pair);
        List<String> pollList = pollcache.get(pair);
        if (CollectionsUtil.isNotEmpty(pollList) && !backAddress.containsAll(pollList)) {
            pollcache.remove(pair);
        }
        initCache(pair);
        List<String> addressList = pollcache.get(pair);
        String address = addressList.get(0);
        addressList.remove(0);
        if (addressList.size() == 0) {
            pollcache.remove(pair);
        }
        return address;
    }

}
