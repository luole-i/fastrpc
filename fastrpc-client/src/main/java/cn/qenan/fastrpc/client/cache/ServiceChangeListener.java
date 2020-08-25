package cn.qenan.fastrpc.client.cache;

import cn.qenan.fastrpc.common.util.StringUtil;
import org.I0Itec.zkclient.IZkChildListener;


import java.util.List;

/**
 * zookeeper监听器
 *
 */

public class ServiceChangeListener implements IZkChildListener{
    private  ManageServiceAddressCache manageServiceAddressCache;

    private ServiceChangeListener(){};

    private final static ServiceChangeListener serviceChangeListener = new ServiceChangeListener();

    public  void register(ManageServiceAddressCache manageServiceAddressCache) {
        this.manageServiceAddressCache = manageServiceAddressCache;
    }

    @Override
    public void handleChildChange(String s, List<String> list) {
        manageServiceAddressCache.serviceChange(StringUtil.split(s, "/")[1]);
    }

    public static ServiceChangeListener getSingleListener(){
        return serviceChangeListener;
    }
}
