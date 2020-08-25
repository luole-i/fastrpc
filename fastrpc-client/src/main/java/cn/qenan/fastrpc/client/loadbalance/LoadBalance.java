package cn.qenan.fastrpc.client.loadbalance;

/**
 * 负载均衡接口
 *
 * @author luolei
 * @version 1.0
 */
public interface LoadBalance {
    String acquireAddress(String serviceName, String version);
}
