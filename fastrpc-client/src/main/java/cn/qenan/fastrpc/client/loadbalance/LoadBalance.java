package cn.qenan.fastrpc.client.loadbalance;

public interface LoadBalance {
    String acquireAddress(String serviceName) throws NullPointerException;

    String acquireAddress(String serviceName, String version) throws NullPointerException;
}
