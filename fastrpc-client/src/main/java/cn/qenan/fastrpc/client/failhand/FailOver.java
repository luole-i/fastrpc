package cn.qenan.fastrpc.client.failhand;

import cn.qenan.fastrpc.client.FastRpcRemote;
import cn.qenan.fastrpc.client.cache.DefaultServiceAddressCache;
import cn.qenan.fastrpc.client.cache.ManageServiceAddressCache;
import cn.qenan.fastrpc.common.beans.FastRpcRequest;
import cn.qenan.fastrpc.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FailOver {
    private final static Logger LOGGER = LoggerFactory.getLogger(FailOver.class);

    public static Object execute(Exception e, FastRpcRequest request, String serviceAddress) {
        ManageServiceAddressCache manageCache = DefaultServiceAddressCache.getSingletonCache();
        String serviceName = request.getInterfaceName();
        if (StringUtil.isNotEmpty(request.getServiceName())) {
            serviceName = request.getServiceName();
        }
        manageCache.removeServiceAddress(serviceName, serviceAddress, request.getServiceVersion());
        String newServiceAddress = null;
        //进行可用地址遍历直到所有可用地址遍历完，ExceptionHandler处理空服务地址异常
        try {
            newServiceAddress = FastRpcRemote.getRemote()
                    .getServiceAddress(request.getServiceName()
                            , Class.forName(request.getInterfaceName())
                            , request.getServiceVersion());
            return FastRpcRemote.getRemote().requestToSend(newServiceAddress, request);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return null;
    }
}
