package cn.qenan.fastrpc.client.loadbalance;

import cn.qenan.fastrpc.common.properties.ClientProperties;
import cn.qenan.fastrpc.common.properties.FastRpcConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 根据配置生成LoadBalanceContext
 *
 * @author luolei
 * @version 1.0
 * <p>
 * 2019/4/28
 */
public class LoadBalanceFactory {
    private final static Logger LOGGER = LoggerFactory.getLogger(LoadBalanceFactory.class);

    private LoadBalanceFactory() {
    }

    private static LoadBalanceContext loadBalanceContext;

    private static String type = FastRpcConfigurer.getProperty(ClientProperties.SERVICE_ADDRESS_LOADBALABCE);

    static {
        LoadBalance loadBalance;
        switch (type) {
            case "random":
                loadBalance = new RandomStrategy();
                LOGGER.debug("load balance strategy is : {}", "random");
                break;
            case "weight":
                loadBalance = new WeightPollStrategy();
                LOGGER.debug("load balance strategy is : {}", "weight");
                break;
            default:
                loadBalance = new PollStrategy();
                LOGGER.debug("load balance strategy is : {}", "poll");
        }
        loadBalanceContext = new LoadBalanceContext(loadBalance);
    }

    public static LoadBalanceContext getLoadBalanceContext() {
        return loadBalanceContext;
    }
}
