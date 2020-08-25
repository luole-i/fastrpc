package cn.qenan.fastrpc.client.cache;

import cn.qenan.fastrpc.common.properties.ClientProperties;
import cn.qenan.fastrpc.common.properties.FastRpcConfigurer;
import cn.qenan.fastrpc.common.util.StringUtil;
import cn.qenan.fastrpc.registry.AddressJson;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DirectServiceAddressCache extends AbstractServiceAddressCache {
    private final static Logger LOGGER = LoggerFactory.getLogger(DirectServiceAddressCache.class);

    private String directAddress = FastRpcConfigurer.getProperty(ClientProperties.SERVICE_ADDRESS);

    private static boolean isDetail = false;

    private static List<String> anyServiceAddressList = new ArrayList<>();

    private volatile static DirectServiceAddressCache singletonsCache;

    private DirectServiceAddressCache() {
        if(StringUtil.isEmpty(directAddress)){
            throw new RuntimeException("configuration error >>> both the registry and the direct service address cannot be empty");
        }
        if (StringUtil.contains(directAddress, "$")) {
            isDetail = true;
            String[] services = StringUtil.split(directAddress, ";");
            for (String service : services) {
                //获取服务名，版本
                String[] serviceNameAndVersion = StringUtil.split(service, "$")[0].split(":");
                String serviceName = serviceNameAndVersion[0];
                String version = "";
                if (serviceNameAndVersion.length > 1) {
                    version = serviceNameAndVersion[1];
                }
                //获取服务地址
                String addressesStr = StringUtil.split(service, "$")[1];
                String[] addresses = StringUtil.split(addressesStr, "-");
                List<String> addressList = new ArrayList<>();
                for (String address : addresses) {
                    addressList.add(getJsonAddress(address, "1", version));
                }
                if (!cache.containsKey(serviceName)) {
                    cache.put(serviceName, addressList);
                    continue;
                }
                addressList.addAll(cache.get(serviceName));
                cache.put(serviceName, addressList);
            }
        } else {
            String[] anyServiceAddresses = StringUtil.split(directAddress, ";");
            anyServiceAddressList = Arrays.asList(anyServiceAddresses);
        }
    }

    //不加锁，相同服务直接覆盖
    @Override
    public void manageCache(String serviceName) {
        if (!isDetail) {
            List<String> addressList = new ArrayList<>();
            for (String address : anyServiceAddressList) {
                addressList.add(getJsonAddress(address, "1", ""));
            }
            cache.put(serviceName, addressList);
        }
    }

    private String getJsonAddress(String address, String weight, String version) {
        JSONObject addresJson = new JSONObject();
        addresJson.put(AddressJson.ADDRESS, address);
        addresJson.put(AddressJson.WEIGHT, weight);
        addresJson.put(AddressJson.VERSION, version);
        return addresJson.toJSONString();
    }

    public static DirectServiceAddressCache getSingletonCache() {
        if (singletonsCache == null) {
            synchronized (DefaultServiceAddressCache.class) {
                if (singletonsCache == null) {
                    singletonsCache = new DirectServiceAddressCache();
                }
            }
        }
        return singletonsCache;
    }

    @Override
    public List<String> acquireServiceList(String serviceName, String version){
        return acquireServiceListFromCache(serviceName, version);
    }
}
