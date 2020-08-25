package cn.qenan.fastrpc.client.zk;

import cn.qenan.fastrpc.client.cache.ServiceChangeListener;
import cn.qenan.fastrpc.common.properties.ClientProperties;
import cn.qenan.fastrpc.common.properties.FastRpcConfigurer;
import cn.qenan.fastrpc.common.properties.ServerProperties;
import cn.qenan.fastrpc.common.util.StringUtil;
import cn.qenan.fastrpc.registry.ServiceDiscovery;
import cn.qenan.fastrpc.registry.ZKConfig;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于zk实现
 *
 * @author luolei
 * @version 1.0
 * <p>
 * 2019/04/11
 */
public class ZkServiceDiscovery implements ServiceDiscovery {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZkServiceDiscovery.class);

    private ZkServiceDiscovery() {
        connectZK();
    }

    private volatile static ZkServiceDiscovery zkServiceDiscovery;

    private ZkClient zkClient;

    private void connectZK() {
        String zkAddress = FastRpcConfigurer.getProperty(ClientProperties.REGISTRY_ADDRESS);
        if (StringUtil.isEmpty(zkAddress)) {
            throw new NullPointerException("zookeeper address can not null");
        }
        int zkSessionTimeout = ZKConfig.ZK_SESSION_TIMEOUT_DEFAULT;
        int zkConnectionTimeout = ZKConfig.ZK_CONNECTION_TIMEOUT_DEFAULT;
        if (FastRpcConfigurer.isContains(ServerProperties.ZK_SESSION_TIMEOUT)) {
            zkSessionTimeout = Integer.valueOf(FastRpcConfigurer.getProperty(ClientProperties.ZK_SESSION_TIMEOUT));
        }
        if (FastRpcConfigurer.isContains(ServerProperties.ZK_CONNECTION_TIMOUT)) {
            zkConnectionTimeout = Integer.valueOf(FastRpcConfigurer.getProperty(ClientProperties.ZK_CONNECTION_TIMOUT));
        }
        zkClient = new ZkClient(zkAddress, zkSessionTimeout, zkConnectionTimeout);
        LOGGER.debug("connect zookeeper : {}", zkAddress);
    }

    public List<String> discover(String serviceName) {
        String path = ZKConfig.ZK_REGISTRY_PATH + "/" + serviceName;
        if (!zkClient.exists(path)) {
            LOGGER.error("can not find the service: {} on registry", serviceName);
            return null;
        }
        IZkChildListener serviceChangeListener = ServiceChangeListener.getSingleListener();
        zkClient.subscribeChildChanges(path,serviceChangeListener);
        List<String> zkNodeList = zkClient.getChildren(path);
        List<String> addressList = new ArrayList<>();
        for (int i = 0; i < zkNodeList.size(); i++) {
            String addressJson = zkClient.readData(path + "/" + zkNodeList.get(i));
            addressList.add(addressJson);
        }
        return addressList;
    }

    public static ZkServiceDiscovery getSingletonDiscovery() {
        if (zkServiceDiscovery == null) {
            synchronized (ZkServiceDiscovery.class) {
                if (zkServiceDiscovery == null) {
                    zkServiceDiscovery = new ZkServiceDiscovery();
                }
            }
        }
        return zkServiceDiscovery;
    }
}
