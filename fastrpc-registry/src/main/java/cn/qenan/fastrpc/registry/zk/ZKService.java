package cn.qenan.fastrpc.registry.zk;

import cn.qenan.fastrpc.common.properties.FastRpcConfigurer;
import cn.qenan.fastrpc.common.properties.FastRpcProperties;
import cn.qenan.fastrpc.common.util.StringUtil;
import org.I0Itec.zkclient.ZkClient;

/**
 * zookeeper服务抽象类，提供客户端连接zookeeper功能
 *
 * @author luolei
 * @version 1.0
 *
 * 2019/04/21
 */
public abstract class ZKService {

    protected ZkClient zkClient;

    protected String connectZK() {
        String zkAddress = FastRpcConfigurer.getProperty(FastRpcProperties.REGISTRY_ADDRESS);
        if (StringUtil.isEmpty(zkAddress)) {
            throw new NullPointerException("zookeeper address can not null");
        }
        int zkSessionTimeout = ZKConfig.ZK_SESSION_TIMEOUT_DEFAULT;
        int zkConnectionTimeout = ZKConfig.ZK_CONNECTION_TIMEOUT_DEFAULT;
        if (FastRpcConfigurer.isContains(FastRpcProperties.ZK_SESSION_TIMEOUT)) {
            zkSessionTimeout = Integer.valueOf(FastRpcConfigurer.getProperty(FastRpcProperties.ZK_SESSION_TIMEOUT));
        }
        if (FastRpcConfigurer.isContains(FastRpcProperties.ZK_CONNECTION_TIMOUT)) {
            zkConnectionTimeout = Integer.valueOf(FastRpcConfigurer.getProperty(FastRpcProperties.ZK_CONNECTION_TIMOUT));
        }
        zkClient = new ZkClient(zkAddress, zkSessionTimeout, zkConnectionTimeout);
        return zkAddress;
    }
}
