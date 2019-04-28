package cn.qenan.fastrpc.client;

import cn.qenan.fastrpc.common.beans.FastRpcRequest;
import cn.qenan.fastrpc.common.beans.FastRpcResponse;
import cn.qenan.fastrpc.common.util.StringUtil;
import cn.qenan.fastrpc.registry.ServiceDiscovery;
import cn.qenan.fastrpc.registry.zk.ZkServiceDiscovery;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * 用于创建rpc服务，代理
 *
 * @author luolei
 * @version 1.0
 * <p>
 * 2019/04/13
 */
@Component
public class FastRpcRemote {

    private static final Logger LOGGER = LoggerFactory.getLogger(FastRpcRemote.class);

    private String serviceAddress;

    private ServiceDiscovery serviceDiscovery;

    @Autowired
    public FastRpcRemote(ZkServiceDiscovery serviceDiscovery){
        this.serviceDiscovery = serviceDiscovery;
    }

    public <T> T create(final Class<?> interfaceClass) {
        return create(interfaceClass, "");
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass, final String serviceVersion) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader()
                , new Class<?>[]{interfaceClass}
                , new InvocationHandler() {
                    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                        FastRpcRequest request = new FastRpcRequest();
                        request.setRequstId(UUID.randomUUID().toString());
                        request.setInterfaceName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setServiceVersion(serviceVersion);
                        request.setParameterTpyes(method.getParameterTypes());
                        request.setParameters(objects);

                        if (serviceDiscovery != null) {
                            String serviceName = interfaceClass.getName();
                            serviceAddress = serviceDiscovery.discover(serviceName,serviceVersion);
                            if (StringUtil.isEmpty(serviceAddress)) {
                                LOGGER.error("service {] is empty", serviceName);
                                throw new RuntimeException("service addres is empty");
                            }
                        }
                        /**todo:结果缓存*/
                        String[] serverArr = StringUtil.split(serviceAddress, ":");
                        String ip = serverArr[0];
                        int port = Integer.valueOf(serverArr[1]);
                        FastRpcClient fastRpcClient = new FastRpcClient(ip, port);
                        long time = System.currentTimeMillis();
                        FastRpcResponse response = fastRpcClient.send(request);
                        LOGGER.debug("request time :{}", System.currentTimeMillis() - time);
                        if (response == null) {
                            throw new RuntimeException("response is null");
                        }
                        if (response.getException() != null) {
                            throw response.getException();
                        } else {
                            return response.getResult();
                        }
                    }
                });
    }
}
