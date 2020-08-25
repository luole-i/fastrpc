package cn.qenan.fastrpc.common.coder;

import cn.qenan.fastrpc.common.serialization.SerializationUtil;
import java.util.List;

public class ClientDecoder extends AbstractDecoder {

    private Class<?> c;

    public ClientDecoder(Class<?> c) {
        this.c = c;
    }

    @Override
    protected void addData(byte[] data, List<Object> list) {
        list.add(SerializationUtil.CLIENT.deserialize(data, c));
    }
}
