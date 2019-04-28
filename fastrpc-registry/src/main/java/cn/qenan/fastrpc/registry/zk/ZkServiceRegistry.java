package cn.qenan.fastrpc.registry.zk;

import cn.qenan.fastrpc.common.properties.FastRpcConfigurer;
import cn.qenan.fastrpc.common.properties.FastRpcProperties;
import cn.qenan.fastrpc.common.util.IntegerUtil;
import cn.qenan.fastrpc.registry.ServiceRegistry;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * 基于Zookeeper实现
 *
 * @author luolei
 * @version 1.0
 * 2019/04/11
 */

@Component
public class ZkServiceRegistry extends ZKService implements ServiceRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZkServiceRegistry.class);

    private static String serverWeight = FastRpcConfigurer.getProperty(FastRpcProperties.SERVICE_WEIGHT);

    public ZkServiceRegistry() {
        String zkAddress = connectZK();
        LOGGER.debug("registry connect zookeeper: {}", zkAddress);
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
        json.put("address", address);
        json.put("version", version);
        if (!IntegerUtil.isValidPositiveInt(serverWeight)) {
            serverWeight = "1";
        }
        json.put("weight", serverWeight);
        String addressNode = zkClient.createEphemeralSequential(path + "/address-", json.toJSONString());
        LOGGER.debug("create address zknode: {}", json.toJSONString());
    }
}
