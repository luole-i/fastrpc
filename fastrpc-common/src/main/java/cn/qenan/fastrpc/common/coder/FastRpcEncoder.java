package cn.qenan.fastrpc.common.coder;

import cn.qenan.fastrpc.common.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class FastRpcEncoder extends MessageToByteEncoder {
    private Class<?> c;

    public FastRpcEncoder(Class<?> c) {
        this.c = c;
    }

    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if(c.isInstance(o)){
            byte[] data = SerializationUtil.serialize(o);
            byteBuf.writeInt(data.length);
            byteBuf.writeBytes(data);
        }
    }
}
