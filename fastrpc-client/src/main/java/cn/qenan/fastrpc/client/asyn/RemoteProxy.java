package cn.qenan.fastrpc.client.asyn;

import cn.qenan.fastrpc.client.Remote;
import cn.qenan.fastrpc.client.failhand.ExceptionHandler;
import cn.qenan.fastrpc.client.loadbalance.LoadBalanceContext;
import cn.qenan.fastrpc.client.loadbalance.LoadBalanceFactory;
import cn.qenan.fastrpc.common.beans.FastRpcRequest;
import cn.qenan.fastrpc.common.beans.FastRpcResponse;
import cn.qenan.fastrpc.common.util.ProtocolUtil;
import cn.qenan.fastrpc.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RemoteProxy implements Remote {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteProxy.class);

    private volatile static RemoteProxy remoteProxy;

    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    private RemoteProxy() {
    }

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
                        String[] serverArr = StringUtil.split(serviceAddress, ":");
                        String ip = serverArr[0];
                        int port = Integer.valueOf(serverArr[1]);
                        InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, port);
                        FastRpcCallable fastRpcCallable = new FastRpcCallable(request, inetSocketAddress);
                        Future<FastRpcResponse> responseFuture = executorService.submit(fastRpcCallable);
                        FastRpcResponse fastRpcResponse = responseFuture.get();
                        if (fastRpcResponse != null) {
                            if (fastRpcResponse.getException() != null) {
                                ExceptionHandler.handler(fastRpcResponse.getException());
                                throw new RuntimeException(fastRpcResponse.getException());
                            }
                            return fastRpcResponse.getResult();
                        }
                        return null;
                    }
                });
    }

    private String getServiceAddress(String serviceName, Class<?> interfaceClass, String serviceVersion) {
        LoadBalanceContext loadBalanceContext = LoadBalanceFactory.getLoadBalanceContext();
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

    public static RemoteProxy getRemote() {
        if (remoteProxy == null) {
            synchronized (RemoteProxy.class) {
                if (remoteProxy == null) {
                    remoteProxy = new RemoteProxy();
                }
            }
        }
        return remoteProxy;
    }
}
