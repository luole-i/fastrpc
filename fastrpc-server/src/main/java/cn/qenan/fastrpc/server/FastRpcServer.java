package cn.qenan.fastrpc.server;

import cn.qenan.fastrpc.common.beans.FastRpcRequest;
import cn.qenan.fastrpc.common.beans.FastRpcResponse;
import cn.qenan.fastrpc.common.coder.FastRpcDecoder;
import cn.qenan.fastrpc.common.coder.FastRpcEncoder;
import cn.qenan.fastrpc.common.properties.FastRpcConfigurer;
import cn.qenan.fastrpc.common.properties.FastRpcProperties;
import cn.qenan.fastrpc.common.util.MapUtil;
import cn.qenan.fastrpc.common.util.StringUtil;
import cn.qenan.fastrpc.registry.ServiceRegistry;
import cn.qenan.fastrpc.registry.zk.ZkServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * RPC服务器
 *
 * @author luolei
 * @version 1.0
 */
@Component
public class FastRpcServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FastRpcServer.class);

    private String serviceAddress;

    private ServiceRegistry serviceRegistry;

    private Map<Pair<String, String>, Object> handlermap = new HashMap<Pair<String, String>, Object>();

    @Autowired
    public FastRpcServer(ZkServiceRegistry serviceRegistry) {
        serviceAddress = FastRpcConfigurer.getProperty(FastRpcProperties.SERVICE_ADDRESS);
        this.serviceRegistry = serviceRegistry;
    }

    public void start(ApplicationContext ctx) throws Exception {
        initLoadService(ctx);
        startServer();
    }

    private void initLoadService(ApplicationContext ctx) throws BeansException {
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(FastRpcService.class);
        if (MapUtil.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                FastRpcService rpcService = serviceBean.getClass().getAnnotation(FastRpcService.class);
                String serviceName = rpcService.value().getName();
                String serviceVersion = rpcService.version();
                Pair<String, String> service = new Pair<String, String>(serviceName, serviceVersion);
                handlermap.put(service, serviceBean);
            }
        }
    }

    private void startServer() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
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
                for (Pair<String, String> service : handlermap.keySet()) {
                    serviceRegistry.register(service.getKey(), serviceAddress, service.getValue());
                    LOGGER.debug("register service:{} version:{} on service address: {}"
                            , service.getKey(), service.getValue(), serviceAddress);
                }
            }
            LOGGER.debug("fastrpc server started on port: {}", port);
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }


    }

}
