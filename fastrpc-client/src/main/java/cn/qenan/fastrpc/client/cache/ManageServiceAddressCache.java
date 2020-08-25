package cn.qenan.fastrpc.client.cache;

/**
 * 管理缓存接口
 *
 * @author luolei
 * @version 1.0
 * <p>
 * 2019/04/27
 */
public interface ManageServiceAddressCache {

    void serviceChange(String serviceName);

    void removeServiceAddress(String serviceName, String serviceAddress,String version);
}
