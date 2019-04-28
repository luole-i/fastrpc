package cn.qenan.fastrpc.server;

import cn.qenan.fastrpc.common.beans.FastRpcRequest;
import cn.qenan.fastrpc.common.beans.FastRpcResponse;
import cn.qenan.fastrpc.common.util.StringUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.util.Pair;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class FastRpcServerHandler extends SimpleChannelInboundHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(FastRpcServerHandler.class);

    private final Map<Pair<String, String>, Object> handlerMap;

    public FastRpcServerHandler(Map<Pair<String, String>, Object> handlermap) {
        this.handlerMap = handlermap;
    }

    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
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
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private Object handler(FastRpcRequest request) throws InvocationTargetException {
        String serviceName = request.getInterfaceName();
        String excptionName = serviceName;
        String serviceVersion = request.getServiceVersion();
        Pair<String,String> service = new Pair<String, String>(serviceName,serviceVersion);
        Object serviceBean = handlerMap.get(service);
        if (serviceBean == null) {
            throw new RuntimeException("can not find service :" + excptionName);
        }
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] paramterTypes = request.getParameterTpyes();
        Object[] paramters = request.getParameters();
        FastClass fastClass = FastClass.create(serviceClass);
        FastMethod fastMethod = fastClass.getMethod(methodName, paramterTypes);
        return fastMethod.invoke(serviceBean, paramters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("server caught exception", cause);
        ctx.close();
    }

}
