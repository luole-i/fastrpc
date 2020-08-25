package cn.qenan.fastrpc.server;

import cn.qenan.fastrpc.common.beans.FastRpcRequest;
import cn.qenan.fastrpc.common.beans.FastRpcResponse;
import cn.qenan.fastrpc.common.exception.ServerException;
import cn.qenan.fastrpc.common.exception.ServerExceptionStat;
import cn.qenan.fastrpc.common.properties.FastRpcConfigurer;
import cn.qenan.fastrpc.common.properties.ServerProperties;
import cn.qenan.fastrpc.common.util.StringUtil;
import cn.qenan.fastrpc.server.gateway.RateLimiterManage;
import cn.qenan.fastrpc.server.gateway.VerifyIp;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 网关处理Handler，主要用来限流还有验证IP是否通过
 */
public class FastRpcGatewayHandler extends SimpleChannelInboundHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(FastRpcGatewayHandler.class);

    private static String qps = FastRpcConfigurer.getProperty(ServerProperties.QPS);

    private static String allowableIP = FastRpcConfigurer.getProperty(ServerProperties.ALLOWABLE_IP);

    protected void channelRead0(ChannelHandlerContext ctx, Object o) {
        if (StringUtil.isNotEmpty(allowableIP)) {
            String ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getHostName();
            if (!VerifyIp.isExist(ip)) {
                LOGGER.warn("illegal IP address:{}", ip);
                String exceptionInfo = "refuse the service cause no access";
                returnException(ctx, (FastRpcRequest) o, exceptionInfo);
            }
        }
        if (StringUtil.isNotEmpty(qps)) {
            RateLimiterManage rateLimiterManage = RateLimiterManage.getSingleRateLimiterManage();
            if (!rateLimiterManage.acquire()) {
                LOGGER.warn("rate limit");
                String exceptionInfo = "refuse the service cause rate limit";
                returnException(ctx, (FastRpcRequest) o, exceptionInfo);
            }
        }
        ctx.fireChannelRead(o);
    }

    private void returnException(ChannelHandlerContext ctx, FastRpcRequest o, String exceptionInfo) {
        FastRpcResponse response = new FastRpcResponse();
        FastRpcRequest request = o;
        response.setRequestId(request.getRequstId());
        response.setException(new ServerException(exceptionInfo, ServerExceptionStat.REFUSE));
        ctx.channel().writeAndFlush(response);
    }
}
