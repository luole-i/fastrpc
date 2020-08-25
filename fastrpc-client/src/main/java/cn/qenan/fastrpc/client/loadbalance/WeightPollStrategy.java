package cn.qenan.fastrpc.client.loadbalance;

import cn.qenan.fastrpc.registry.AddressJson;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据权值轮询轮询获取服务地址
 *
 * @author luolei
 * @version 1.0
 */
public class WeightPollStrategy extends PollStrategy {

    @Override
    protected synchronized void initCache(Pair<String, String> pair) {
        if (!pollcache.containsKey(pair)) {
            List<String> list = backCache.get(pair);
            List<String> addressList = new ArrayList<>();
            for (String x : list) {
                JSONObject addressJson = JSON.parseObject(x);
                int weight = Integer.valueOf(addressJson.getString(AddressJson.WEIGHT));
                for (int i = 0; i < weight; i++) {
                    addressList.add(x);
                }
            }
            pollcache.put(pair, addressList);
        }
    }
}
