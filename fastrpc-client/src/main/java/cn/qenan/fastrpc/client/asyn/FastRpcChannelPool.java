package cn.qenan.fastrpc.client.asyn;

import cn.qenan.fastrpc.client.failhand.ExceptionHandler;
import cn.qenan.fastrpc.common.beans.FastRpcRequest;
import cn.qenan.fastrpc.common.beans.FastRpcResponse;
import cn.qenan.fastrpc.common.coder.ClientDecoder;
import cn.qenan.fastrpc.common.coder.ClientEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Channel工厂，提供获取Channel，释放Channel，维护Channel的长连接
 *
 * @author 罗磊
 * @version 1.0
 */
public class FastRpcChannelPool {
    private final static Logger LOGGER = LoggerFactory.getLogger(FastRpcChannelPool.class);

    private static FastRpcChannelPool pool = new FastRpcChannelPool();

    //默认每个IP可包含10个channel通道
    private int channelSize = 10;

    //不同的IP用自己的Channel队列
    private HashMap<InetSocketAddress, LinkedBlockingQueue<Channel>> channelMap = new HashMap<>();

    private FastRpcChannelPool(){}

    public static FastRpcChannelPool getPool(){
        return pool;
    }

    /**
     * 获取Channel
     *
     * @param address 传入服务地址
     * @return Channel
     */
    public Channel acquire(InetSocketAddress address) {
        Channel resultChannel = null;
        LinkedBlockingQueue<Channel> queue = null;
        if (!channelMap.containsKey(address)) {
            queue = new LinkedBlockingQueue<>(channelSize);
            channelMap.put(address,queue);
        } else {
            queue = channelMap.get(address);
        }
        int queueSize = queue.size();
        //通过非阻塞获取channel
        resultChannel = queue.poll();
        //判断channel是否为空
        if (resultChannel == null) {
            //如果当前服务地址的channel小于允许长度，则创建新channel，否则阻塞获取channel。
            if (queueSize < channelSize) {
                //创建channel并返回channel
                try {
                    Channel channel = null;
                    while (channel == null) {
                        channel = createChannel(address);
                    }
                    return channel;
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage());
                    e.printStackTrace();
                }
            } else {
                try {
                    resultChannel = queue.take();
                } catch (InterruptedException e) {
                    LOGGER.error("thread:{} acquire InetSocketAddress channel failure", Thread.currentThread().getName());
                    e.printStackTrace();
                }
            }
        }
        return resultChannel;
    }

    public void release(Channel channel, InetSocketAddress address) {
        LinkedBlockingQueue queue = channelMap.get(address);
        try {
            if (channel == null || !channel.isActive() || !channel.isOpen() || !channel.isWritable()) {
                if (channel != null) {
                    channel.deregister().syncUninterruptibly().awaitUninterruptibly();
                    channel.closeFuture().syncUninterruptibly().awaitUninterruptibly();
                }
                Channel newChannel = null;
                while (newChannel == null) {
                    newChannel = createChannel(address);
                }
                LOGGER.debug("register new Channel to {}", address);
                queue.put(newChannel);
                return;
            }
            queue.put(channel);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建channel
     *
     * @param address 服务地址
     * @return 返回创建成功的channel
     */
    private Channel createChannel(InetSocketAddress address) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup(channelSize);
        Bootstrap boot = new Bootstrap();
        boot.group(group);
        boot.channel(NioSocketChannel.class);
        boot.handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast(new ClientEncoder(FastRpcRequest.class))
                        .addLast(new ClientDecoder(FastRpcResponse.class))
                        .addLast(new FastRpcClientHandler());
            }
        });
        boot.remoteAddress(address);
        ChannelFuture channelFuture = boot.connect().sync();
        final Channel channel = channelFuture.channel();
        final BooleanReference isAcquireSucceed = new BooleanReference(false);
        //等待监听回调成功
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    isAcquireSucceed.setTrue();
                } else {
                    isAcquireSucceed.setFalse();
                    LOGGER.error(channelFuture.cause().getMessage());
                }
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
        if (isAcquireSucceed.get() == true) {
            return channel;
        }
        return null;
    }

    //boolean引用类，方便final修饰时可改变引用的内容
    private static class BooleanReference {
        private boolean flag;

        private BooleanReference(boolean flag) {
            this.flag = flag;
        }

        private void setTrue() {
            this.flag = true;
        }

        private void setFalse() {
            this.flag = false;
        }

        private boolean get() {
            return flag;
        }
    }
}
