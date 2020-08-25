package cn.qenan.fastrpc.client.asyn;

import cn.qenan.fastrpc.common.beans.FastRpcRequest;
import cn.qenan.fastrpc.common.beans.FastRpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;

public class FastRpcCallable implements Callable<FastRpcResponse> {
    private final static Logger LOGGER = LoggerFactory.getLogger(FastRpcCallable.class);

    private FastRpcRequest request;

    private InetSocketAddress address;

    private FastRpcChannelPool fastRpcChannelPool = FastRpcChannelPool.getPool();

    public FastRpcCallable(FastRpcRequest request,InetSocketAddress address){
        this.request = request;
        this.address = address;
    }

    @Override
    public FastRpcResponse call() throws Exception {
        Channel channel = fastRpcChannelPool.acquire(address);
        try{
            while (!channel.isOpen() || !channel.isActive() || !channel.isWritable()) {
                channel = fastRpcChannelPool.acquire(address);
            }
            FastRpcResultPool.init(request.getRequstId());
            ChannelFuture channelFuture = channel.writeAndFlush(request);
            channelFuture.syncUninterruptibly();

            FastRpcResponse response = FastRpcResultPool.getResult(request.getRequstId());
            if(response==null){
                throw new RuntimeException("请求超时");
            }
            return response;
        }finally {
            fastRpcChannelPool.release(channel,address);
        }
    }
}
