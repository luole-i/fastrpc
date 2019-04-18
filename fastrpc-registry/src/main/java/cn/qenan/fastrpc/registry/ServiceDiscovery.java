package cn.qenan.fastrpc.registry;

import java.net.InetAddress;

/**
 * 服务发现端口
 *
 * @author luolei
 * @version 1.0
 *
 * 2019/04/11
 */
public interface ServiceDiscovery {
    /**
     * 根据服务名查找服务地址
     * @param serviceName 服务名称
     * @return 服务地址
     */
    String discover(String serviceName);
}
