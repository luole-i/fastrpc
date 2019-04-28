package cn.qenan.fastrpc.client.loadbalance;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomStrategy extends AbstractLoadBalance {
    private static Logger LOGGER = LoggerFactory.getLogger(RandomStrategy.class);

    @Override
    public String acquireAddress(String serviceName, String version) throws NullPointerException{
        List<String> addressList = cache.acquireServiceList(serviceName, version);
        if (addressList.size() == 0) {
            LOGGER.error("the service:{} has no surviving server", serviceName);
            throw new NullPointerException("the service:"+serviceName+"has no surviving server");
        }
        return addressList.get(ThreadLocalRandom.current().nextInt(addressList.size()));
    }

    @Override
    String getAddressByLoadBlance(Pair<String, String> pair) {
        return null;
    }
}
