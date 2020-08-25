package cn.qenan.fastrpc.common.coder;

import cn.qenan.fastrpc.common.serialization.SerializationUtil;

import java.util.List;

public class ServerDecoder extends AbstractDecoder {

    private Class<?> c;

    public ServerDecoder(Class<?> c){
        this.c = c;
    }

    protected void addData(byte[] data, List<Object> list) {
        list.add(SerializationUtil.SERVER.deserialize(data,c));
    }
}
