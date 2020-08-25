package cn.qenan.fastrpc.server;

import cn.qenan.fastrpc.common.beans.FastRpcRequest;
import cn.qenan.fastrpc.common.beans.FastRpcResponse;
import cn.qenan.fastrpc.common.coder.ServerDecoder;
import cn.qenan.fastrpc.common.coder.ServerEncoder;
import cn.qenan.fastrpc.common.properties.FastRpcConfigurer;
import cn.qenan.fastrpc.common.properties.ServerProperties;
import cn.qenan.fastrpc.common.util.AnnotationUtil;
import cn.qenan.fastrpc.common.util.StringUtil;
import cn.qenan.fastrpc.registry.ServiceRegistry;
import cn.qenan.fastrpc.server.annotation.FastRpcService;
import cn.qenan.fastrpc.server.annotation.FastRpcServiceName;
import cn.qenan.fastrpc.server.zk.ZkRegistryFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RPC服务器
 *
 * @author luolei
 * @version 1.0
 */
public class FastRpcServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FastRpcServer.class);

    private String serviceAddress = FastRpcConfigurer.getProperty(ServerProperties.SERVICE_ADDRESS);

    private String registryStart = FastRpcConfigurer.getProperty(ServerProperties.REGISTRY_IS_START);

    private Map<Pair<String, String>, Object> handlermap = new HashMap<Pair<String, String>, Object>();

    private volatile static FastRpcServer server = null;

    private FastRpcServer() {
    }

    public static void  start() {
        if(server == null){
            synchronized (FastRpcServer.class){
                if(server == null){
                    server = new FastRpcServer();
                    server.initService();
                    server.beginServer();
                }
            }
        }
    }

    /**
     * 初始化服务，保存到handlermap
     */
    private void initService() {
        List<Object> list = AnnotationUtil.getBeanWithAnnotation(FastRpcService.class);
        for (Object o : list) {
            FastRpcService rpcService = o.getClass().getAnnotation(FastRpcService.class);
            String serviceName = rpcService.value().getName();
            String serviceVersion = rpcService.version();
            Pair<String, String> service = new Pair<String, String>(serviceName, serviceVersion);
            handlermap.put(service, o);
        }
        list = AnnotationUtil.getBeanWithAnnotation(FastRpcServiceName.class);
        for (Object o : list) {
            FastRpcServiceName rpcService = o.getClass().getAnnotation(FastRpcServiceName.class);
            String serviceName = rpcService.serviceName();
            String serviceVersion = rpcService.version();
            Pair<String, String> service = new Pair<String, String>(serviceName, serviceVersion);
            handlermap.put(service, o);
        }
    }

    /**
     * 开启服务
     *
     * @throws Exception
     */
    private void beginServer() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap boot = new ServerBootstrap();
        boot.group(bossGroup, workerGroup);
        boot.channel(NioServerSocketChannel.class);
        boot.childHandler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel socketChannel) {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast(new ServerDecoder(FastRpcRequest.class))
                        .addLast(new ServerEncoder(FastRpcResponse.class))
                        .addLast(new FastRpcGatewayHandler())
                        .addLast(new FastRpcServerHandler(handlermap));
            }
        });
        boot.option(ChannelOption.SO_BACKLOG, 1024);
        boot.childOption(ChannelOption.SO_KEEPALIVE, true);
        boot.childOption(ChannelOption.TCP_NODELAY, true);
        String[] addressArr = StringUtil.split(serviceAddress, ":");
        String ip = addressArr[0];
        int port = Integer.valueOf(addressArr[1]);
        try {
            ChannelFuture future = boot.bind(ip, port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (StringUtil.isNotEmpty(registryStart) && Integer.valueOf(registryStart) == 1) {
            publishService();
        }
        LOGGER.debug("fastrpc server started on port: {}", port);
    }

    /**
     * 发布服务到注册中心
     */
    private void publishService() {
        ServiceRegistry serviceRegistry = ZkRegistryFactory.getServiceRegistry();
        if (serviceRegistry != null) {
            for (Pair<String, String> service : handlermap.keySet()) {
                serviceRegistry.register(service.getKey(), serviceAddress, service.getValue());
                LOGGER.debug("register the service:{} and version:{} to the address : {}"
                        , service.getKey(), service.getValue(), serviceAddress);
            }
        }
    }
}
