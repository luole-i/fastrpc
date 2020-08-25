package cn.qenan.fastrpc.client.loadbalance;

/**
 * 获取负载均衡算法，策略模式，对外提供通过负载均衡获取服务地址
 *
 * @author luolei
 * @version 1.0
 * <p>
 * 2019/4/27
 */
public class LoadBalanceContext {
    private LoadBalance loadBalance;

    public LoadBalanceContext(LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }

    public String acquireAddress(Class<?> cls, String version){
        return loadBalance.acquireAddress(cls.getName(), version);
    }

    public String acquireAddress(String serviceName,String version){
        return loadBalance.acquireAddress(serviceName, version);
    }
}
