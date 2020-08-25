package cn.qenan.fastrpc.server;

import cn.qenan.fastrpc.common.beans.FastRpcRequest;
import cn.qenan.fastrpc.common.beans.FastRpcResponse;
import cn.qenan.fastrpc.common.exception.ServerException;
import cn.qenan.fastrpc.common.exception.NullServiceException;
import cn.qenan.fastrpc.common.exception.ServerExceptionStat;
import cn.qenan.fastrpc.common.util.StringUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.util.Pair;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

public class FastRpcServerHandler extends SimpleChannelInboundHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FastRpcServerHandler.class);

    private final Map<Pair<String, String>, Object> handlerMap;

    public FastRpcServerHandler(Map<Pair<String, String>, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    protected void channelRead0(ChannelHandlerContext ctx, Object o) {
        FastRpcResponse response = new FastRpcResponse();
        FastRpcRequest request = (FastRpcRequest) o;
        response.setRequestId(request.getRequstId());
        try {
            Object result = handler(request);
            response.setResult(result);
        } catch (Exception e) {
            LOGGER.error("handle result failure", e);
            response.setException(e);
        }
        ctx.writeAndFlush(response);
    }


    private Object handler(FastRpcRequest request) {
        String serviceName = request.getInterfaceName();
        if (StringUtil.isNotEmpty(request.getServiceName())) {
            serviceName = request.getServiceName();
        }
        String serviceVersion = request.getServiceVersion();
        Pair<String, String> service = new Pair<String, String>(serviceName, serviceVersion);
        Object serviceBean = handlerMap.get(service);
        if (serviceBean == null) {
            LOGGER.error("can not find the service: {}", serviceName);
            throw new NullServiceException("can not find the service: " + serviceName);
        }
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] paramterTypes = request.getParameterTypes();
        Object[] paramters = request.getParameters();
        FastClass fastClass = FastClass.create(serviceClass);
        FastMethod fastMethod = fastClass.getMethod(methodName, paramterTypes);
        try {
            return fastMethod.invoke(serviceBean, paramters);
        } catch (Exception e) {
           e.printStackTrace();
        }
        return null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("server caught exception", cause);
        ctx.close();
    }

}
