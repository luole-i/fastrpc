package cn.qenan.fastrpc.registry;

/**
 * 服务注册接口
 *
 * @author luolei
 * @version 1.0
 * 2019/04/11
 */
public interface ServiceRegistry {

    /**
     * @param serviceName 服务名
     * @param address 服务地址
     * @param version 服务版本
     */
    void register(String serviceName,String address,String version);
}
