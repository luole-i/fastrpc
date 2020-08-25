package cn.qenan.fastrpc.server.zk;

import cn.qenan.fastrpc.common.properties.FastRpcConfigurer;
import cn.qenan.fastrpc.common.properties.ServerProperties;
import cn.qenan.fastrpc.common.util.IntegerUtil;
import cn.qenan.fastrpc.common.util.StringUtil;
import cn.qenan.fastrpc.registry.AddressJson;
import cn.qenan.fastrpc.registry.ServiceRegistry;
import cn.qenan.fastrpc.registry.ZKConfig;
import com.alibaba.fastjson.JSONObject;
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
public class ZkServiceRegistry implements ServiceRegistry {
    private ZkServiceRegistry() {
        connectZK();
    }

    private volatile static ZkServiceRegistry zkServiceRegistry;

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkServiceRegistry.class);

    private static String serverWeight = FastRpcConfigurer.getProperty(ServerProperties.SERVICE_WEIGHT);

    private ZkClient zkClient;

    private void connectZK() {
        String zkAddress = FastRpcConfigurer.getProperty(ServerProperties.REGISTRY_ADDRESS);
        if (StringUtil.isEmpty(zkAddress)) {
            throw new NullPointerException("zookeeper address can not null");
        }
        int zkSessionTimeout = ZKConfig.ZK_SESSION_TIMEOUT_DEFAULT;
        int zkConnectionTimeout = ZKConfig.ZK_CONNECTION_TIMEOUT_DEFAULT;
        if (FastRpcConfigurer.isContains(ServerProperties.ZK_SESSION_TIMEOUT)) {
            zkSessionTimeout = Integer.valueOf(FastRpcConfigurer.getProperty(ServerProperties.ZK_SESSION_TIMEOUT));
        }
        if (FastRpcConfigurer.isContains(ServerProperties.ZK_CONNECTION_TIMOUT)) {
            zkConnectionTimeout = Integer.valueOf(FastRpcConfigurer.getProperty(ServerProperties.ZK_CONNECTION_TIMOUT));
        }
        zkClient = new ZkClient(zkAddress, zkSessionTimeout, zkConnectionTimeout);
        LOGGER.debug("connect zookeeper : {}", zkAddress);
    }

    public void register(String serviceName, String address, String version) {
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
        JSONObject json = new JSONObject();
        json.put(AddressJson.ADDRESS, address);
        json.put(AddressJson.VERSION, version);
        if (!IntegerUtil.isValidPositiveInt(serverWeight)) {
            serverWeight = "1";
        }
        json.put(AddressJson.WEIGHT, serverWeight);
        String addressNode = zkClient.createEphemeralSequential(path + "/address-", json.toJSONString());
        LOGGER.debug("create address zknode name : {} , data : {}", addressNode, json.toJSONString());
    }

    public static ZkServiceRegistry getSingletonRegistry() {
        if (zkServiceRegistry == null) {
            synchronized (ZkServiceRegistry.class) {
                if (zkServiceRegistry == null) {
                    zkServiceRegistry = new ZkServiceRegistry();
                }
            }
        }
        return zkServiceRegistry;
    }
}
