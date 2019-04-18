package cn.qenan.fastrpc.registry.zk;

import cn.qenan.fastrpc.registry.ServiceDiscovery;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 基于zk实现
 *
 * @author luolei
 * @version 1.0
 * <p>
 * 2019/04/11
 */
public class ZKServiceDiscovery implements ServiceDiscovery {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZKServiceDiscovery.class);

    private ZkClient zkClient;

    public ZKServiceDiscovery(String zkAddress) {
        zkClient = new ZkClient(zkAddress, ZKConfig.ZK_SESSION_TIMEOUT_DEFAULT, ZKConfig.ZK_CONNECTION_TIMEOUT_DEFAULT);
        LOGGER.debug("connect zookeeper: {}", zkAddress);
    }

    public ZKServiceDiscovery(String zkAddress, int sessionTimeout, int connectionTimeout) {
        zkClient = new ZkClient(zkAddress, sessionTimeout, connectionTimeout);
        LOGGER.debug("connect zookeeper: {}", zkAddress);
    }

    public String discover(String serviceName) {
        try{
            String path = ZKConfig.ZK_REGISTRY_PATH + "/" + serviceName;
            if (!zkClient.exists(path)) {
                LOGGER.error("can not find any service: {}" + serviceName);
                throw new RuntimeException("can not find any service :" + serviceName);
            }
            List<String> addressList = zkClient.getChildren(path);
            if (addressList.size() == 0) {
                LOGGER.error("This service has no surviving zknodes :{}", serviceName);
                throw new RuntimeException("cThis service has no surviving zknodes :" + serviceName);
            }
            String address;
            if(addressList.size()==1){
                address = addressList.get(0);
            }else {
                address = addressList.get(ThreadLocalRandom.current().nextInt(addressList.size()));
            }
            LOGGER.debug("grt a address zknode : {}",address);
            return zkClient.readData(path+"/"+address);
        }finally {
            zkClient.close();
        }

    }

}
