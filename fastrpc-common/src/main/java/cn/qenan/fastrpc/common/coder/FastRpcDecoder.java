package cn.qenan.fastrpc.common.coder;

import cn.qenan.fastrpc.common.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class FastRpcDecoder extends ByteToMessageDecoder {

    private Class<?> c;

    public FastRpcDecoder(Class<?> c){
        this.c = c;
    }


    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes()<4){
            return;
        }
        byteBuf.markReaderIndex();
        int length = byteBuf.readInt();
        if(byteBuf.readableBytes()<length){
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] data = new byte[length];
        byteBuf.readBytes(data);
        list.add(SerializationUtil.deserialize(data,c));
    }
}
