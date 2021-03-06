package cn.qenan.fastrpc.registry;

import java.net.InetAddress;
import java.util.List;

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
     */
    List<String> discover(String serviceName);
}
