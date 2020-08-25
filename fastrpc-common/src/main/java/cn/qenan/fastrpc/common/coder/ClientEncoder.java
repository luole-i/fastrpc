package cn.qenan.fastrpc.common.coder;

import cn.qenan.fastrpc.common.serialization.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ClientEncoder extends MessageToByteEncoder {
    private Class<?> c;

    public ClientEncoder(Class<?> c) {
        this.c = c;
    }

    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if(c.isInstance(o)){
            byte[] data = SerializationUtil.CLIENT.serialize(o);
            byteBuf.writeInt(data.length);
            byteBuf.writeBytes(data);
        }
    }
}
