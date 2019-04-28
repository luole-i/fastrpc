package cn.qenan.fastrpc.client;

import cn.qenan.fastrpc.common.beans.FastRpcRequest;
import cn.qenan.fastrpc.common.beans.FastRpcResponse;
import cn.qenan.fastrpc.common.coder.FastRpcDecoder;
import cn.qenan.fastrpc.common.coder.FastRpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * rpc客户端
 *
 * @author luolei
 * @version 1.0
 * 2019/04/13
 */
public class FastRpcClient extends SimpleChannelInboundHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FastRpcClient.class);

    private final String host;

    private final int port;

    private FastRpcResponse response;

    public FastRpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        this.response = (FastRpcResponse) o;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("api caught exception", cause);
        ctx.close();
    }

    public FastRpcResponse send(FastRpcRequest request) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap boot = new Bootstrap();
            boot.group(group);
            boot.channel(NioSocketChannel.class);
            boot.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new FastRpcEncoder(FastRpcRequest.class))
                            .addLast(new FastRpcDecoder(FastRpcResponse.class))
                            .addLast(FastRpcClient.this);
                }
            });

            boot.option(ChannelOption.TCP_NODELAY, true);
            ChannelFuture future = boot.connect(host, port).sync();
            Channel channel = future.channel();
            channel.writeAndFlush(request).sync();
            channel.closeFuture().sync();
            return response;
        } finally {
            group.shutdownGracefully();
        }
    }
}
