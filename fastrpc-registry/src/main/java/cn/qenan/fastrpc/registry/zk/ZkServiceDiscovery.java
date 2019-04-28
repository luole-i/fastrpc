package cn.qenan.fastrpc.registry.zk;

import cn.qenan.fastrpc.common.properties.FastRpcConfigurer;
import cn.qenan.fastrpc.common.properties.FastRpcProperties;
import cn.qenan.fastrpc.common.util.StringUtil;
import cn.qenan.fastrpc.registry.ServiceDiscovery;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

@Component
public class ZkServiceDiscovery extends ZKService implements ServiceDiscovery {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZkServiceDiscovery.class);


    public ZkServiceDiscovery() {
        String zkAddress = connectZK();
        LOGGER.debug("discovery connect zookeeper: {}", zkAddress);
    }

    public String discover(String serviceName, String version) {
        String path = ZKConfig.ZK_REGISTRY_PATH + "/" + serviceName;
        if (!zkClient.exists(path)) {
            LOGGER.error("can not find any service: {}" + serviceName);
            throw new RuntimeException("can not find any service :" + serviceName);
        }
        List<String> addressList = zkClient.getChildren(path);
        if (addressList.size() == 0) {
            LOGGER.error("This service has no surviving zknodes :{}", serviceName);
            throw new RuntimeException("This service has no surviving zknode :" + serviceName);
        }
        List<String> versionList = new ArrayList<>();
        for (int i = 0; i < addressList.size(); i++) {
            String addressJson = zkClient.readData(path + "/" + addressList.get(i));
            JSONObject jsonObject = JSON.parseObject(addressJson);
            String vers = jsonObject.getString("version");
            if(vers.equals(version)){
                versionList.add(addressList.get(i));
            }
        }
        if (versionList.size() == 0) {
            LOGGER.error("This service has no surviving zknodes :{}", serviceName);
            throw new RuntimeException("This service has no surviving zknodes :" + serviceName);
        }
        String addressNode;
        /*todo:负载均衡策略（可以提到client端实现：缓存地址，无效在进行查询）*/
        if (versionList.size() == 1) {
            addressNode = versionList.get(0);
        } else {
            addressNode = versionList.get(ThreadLocalRandom.current().nextInt(versionList.size()));
        }
        LOGGER.debug("get a address zknode : {}", addressNode);
        String addressJson = zkClient.readData(path + "/" + addressNode);
        JSONObject jsonObject = JSON.parseObject(addressJson);
        return jsonObject.getString("address");
    }

    public List<String> discoverall(String serviceName) {
        String path = ZKConfig.ZK_REGISTRY_PATH + "/" + serviceName;
        if (!zkClient.exists(path)) {
            LOGGER.error("can not find any service:{}" + serviceName);
            return null;
        }
        List<String> zkNodeList = zkClient.getChildren(path);
        List<String> addressList = new ArrayList<>();
        for(int i = 0;i<zkNodeList.size();i++){
            String addressJson = zkClient.readData(path + "/" + zkNodeList.get(i));
            addressList.add(addressJson);
        }
        return addressList;
    }
}
