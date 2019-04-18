package cn.qenan.fastrpc.registry.zk;

import cn.qenan.fastrpc.registry.ServiceRegistry;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 基于Zookeeper实现
 *
 * @author luolei
 * @version 1.0
 * 2019/04/11
 */
public class ZKServiceRegistry implements ServiceRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZKServiceRegistry.class);

    private final ZkClient zkClient;

    public ZKServiceRegistry(String zkAddress) {
        zkClient = new ZkClient(zkAddress, ZKConfig.ZK_SESSION_TIMEOUT_DEFAULT, ZKConfig.ZK_CONNECTION_TIMEOUT_DEFAULT);
        LOGGER.debug("connect zookeeper: {}", zkAddress);
    }

    public ZKServiceRegistry(String zkAddress, int sessionTimeout, int connectionTimeout) {
        zkClient = new ZkClient(zkAddress, sessionTimeout, connectionTimeout);
        LOGGER.debug("connect zookeeper: {}", zkAddress);
    }

    public void register(String serviceName, String address) {
        String path = ZKConfig.ZK_REGISTRY_PATH;
        if (!zkClient.exists(path)) {
            zkClient.createPersistent(path);
            LOGGER.debug("create registry zknode: {}", path);
        }
        //创建service节点
        path = path + "/" + serviceName;
        if (!zkClient.exists(path)) {
            zkClient.createPersistent(path);
            LOGGER.debug("create service zknode: {}", path);
        }
        String addressNode = zkClient.createEphemeralSequential(path + "/address-", address);
        LOGGER.debug("create address zknode: {}", addressNode);
    }
}
