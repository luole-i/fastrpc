package cn.qenan.fastrpc.client.loadbalance;

import javafx.util.Pair;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机获取服务地址策略
 *
 * @author luolei
 * @version 1.0
 * <p>
 * 2019/4/27
 */
public class RandomStrategy extends AbstractLoadBalance {

    @Override
    public String getAddressByLoadBalance(Pair<String, String> pair) {
        List<String> addressList = backCache.get(pair);
        return addressList.get(ThreadLocalRandom.current().nextInt(addressList.size()));
    }


}
