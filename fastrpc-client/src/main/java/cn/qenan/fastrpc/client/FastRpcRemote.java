package cn.qenan.fastrpc.client;

import cn.qenan.fastrpc.client.failhand.ExceptionHandler;
import cn.qenan.fastrpc.client.loadbalance.LoadBalanceContext;
import cn.qenan.fastrpc.client.loadbalance.LoadBalanceFactory;
import cn.qenan.fastrpc.common.beans.FastRpcRequest;
import cn.qenan.fastrpc.common.beans.FastRpcResponse;
import cn.qenan.fastrpc.common.util.ProtocolUtil;
import cn.qenan.fastrpc.common.util.StringUtil;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 用于创建rpc服务，jdk动态代理，使用这个Remote必须有服务端提供的接口api
 *
 * @author luolei
 * @version 1.0
 * <p>
 * 2019/04/13
 */
public class FastRpcRemote implements Remote {
    private static final Logger LOGGER = LoggerFactory.getLogger(FastRpcRemote.class);

    private volatile static FastRpcRemote fastRpcRemote;

    public <T> T create(final Class<?> interfaceClass) {
        return create(interfaceClass, "");
    }


    public <T> T create(final Class<?> interfaceClass, final String serviceVersion) {
        return create(interfaceClass, "", serviceVersion);
    }


    public <T> T createServiceName(final Class<?> interfaceClass, final String serviceName) {
        return create(interfaceClass, serviceName, "");
    }


    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass, final String serviceName, final String serviceVersion) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader()
                , new Class<?>[]{interfaceClass}
                , new InvocationHandler() {
                    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                        FastRpcRequest request = ProtocolUtil.requestConvertToProtocol(serviceName, serviceVersion, method, objects);
                        String serviceAddress = getServiceAddress(serviceName, interfaceClass, serviceVersion);
                        return requestToSend(serviceAddress, request);
                    }
                });
    }

    public String getServiceAddress(String serviceName, Class<?> interfaceClass, String serviceVersion) {
        LoadBalanceContext loadBalanceContext = LoadBalanceFactory.getLoadBalanceContext();
        //捕获服务空地址异常并进行异常处理
        try {
            if (StringUtil.isNotEmpty(serviceName)) {
                return loadBalanceContext.acquireAddress(serviceName, serviceVersion);
            } else {
                return loadBalanceContext.acquireAddress(interfaceClass, serviceVersion);
            }
        } catch (Exception e) {
            ExceptionHandler.handler(e);
            throw e;
        }
    }

    public Object requestToSend(String serviceAddress, FastRpcRequest fastRpcRequest) throws Exception {
        String[] serverArr = StringUtil.split(serviceAddress, ":");
        String ip = serverArr[0];
        int port = Integer.valueOf(serverArr[1]);
        FastRpcClient fastRpcClient = new FastRpcClient(ip, port);
        long time = System.currentTimeMillis();
        FastRpcResponse response = null;
        //捕获连接异常并进行异常处理
        try {
            response = fastRpcClient.send(fastRpcRequest);
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
            return ExceptionHandler.handler(e, fastRpcRequest, serviceAddress);
        }
        LOGGER.debug("method: {} request time: {}", fastRpcRequest.getMethodName(), System.currentTimeMillis() - time);
        if (response == null) {
            throw new RuntimeException("response is null");
        }
        //处理服务端异常
        if (response.getException() != null) {
            ExceptionHandler.handler(response.getException());
            throw response.getException();
        } else {
            return response.getResult();
        }
    }

    public static FastRpcRemote getRemote() {
        if (fastRpcRemote == null) {
            synchronized (FastRpcRemote.class) {
                if (fastRpcRemote == null) {
                    fastRpcRemote = new FastRpcRemote();
                }
            }
        }
        return fastRpcRemote;
    }
}
