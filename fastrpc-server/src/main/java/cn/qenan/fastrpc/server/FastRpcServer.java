package cn.qenan.fastrpc.server;

import cn.qenan.fastrpc.common.beans.FastRpcRequest;
import cn.qenan.fastrpc.common.beans.FastRpcResponse;
import cn.qenan.fastrpc.common.coder.FastRpcDecoder;
import cn.qenan.fastrpc.common.coder.FastRpcEncoder;
import cn.qenan.fastrpc.common.util.MapUtil;
import cn.qenan.fastrpc.common.util.StringUtil;
import cn.qenan.fastrpc.registry.zk.ZKServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * RPC服务器
 *
 * @author luolei
 * @version 1.0
 */
public class FastRpcServer implements ApplicationContextAware, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(FastRpcServer.class);

    private String serviceAddress;

    private ZKServiceRegistry serviceRegistry;

    private Map<String, Object> handlermap = new HashMap<String, Object>();

    public FastRpcServer(String serviceAddress, ZKServiceRegistry serviceRegistry) {
        this.serviceAddress = serviceAddress;
        this.serviceRegistry = serviceRegistry;
    }

    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(FastRpcService.class);
        if (MapUtil.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                FastRpcService rpcService = serviceBean.getClass().getAnnotation(FastRpcService.class);
                String serviceName = rpcService.value().getName();
                String serviceVersion = rpcService.version();
                if (StringUtil.isNotEmpty(serviceVersion)) {
                    serviceName += "-" + serviceVersion;
                }
                handlermap.put(serviceName, serviceBean);
            }
        }
    }

    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap boot = new ServerBootstrap();
            boot.group(bossGroup, workerGroup);
            boot.channel(NioServerSocketChannel.class);
            boot.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new FastRpcDecoder(FastRpcRequest.class))
                            .addLast(new FastRpcEncoder(FastRpcResponse.class))
                            .addLast(new FastRpcServerHandler(handlermap));
                }
            });
            boot.option(ChannelOption.SO_BACKLOG, 1024);
            boot.childOption(ChannelOption.SO_KEEPALIVE, true);
            String[] addressArr = StringUtil.split(serviceAddress, ":");
            String ip = addressArr[0];
            int port = Integer.valueOf(addressArr[1]);
            ChannelFuture future = boot.bind(ip, port).sync();
            if (serviceRegistry != null) {
                for (String serviceName : handlermap.keySet()) {
                    serviceRegistry.register(serviceName,serviceAddress);
                    LOGGER.debug("register service: {} to address: {}",serviceName,serviceAddress);
                }
            }
            LOGGER.debug("rpc server started on port: {}",port);
            future.channel().closeFuture().sync();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }


    }

}
