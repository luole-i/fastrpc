package cn.qenan.fastrpc.common.serialization.impl;

import cn.qenan.fastrpc.common.serialization.Serialization;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;

public class SerializeProtostuff extends Serialization {

    public <T> byte[] serialize(T obj) {
        Class<T> c = (Class<T>)obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try{
            Schema<T> schema = getSchema(c);
            return ProtostuffIOUtil.toByteArray(obj,schema,buffer);
        }catch (Exception e){
            throw new IllegalStateException(e.getMessage(),e);
        }finally {
            buffer.clear();
        }
    }

    public <T> T deserialoze(byte[] data, Class<T> c) {
        try {
            T message = (T) objenesis.newInstance(c);
            Schema<T> schema = (Schema<T>) getSchema(c);
            ProtostuffIOUtil.mergeFrom(data,message,schema);
            return message;
        }catch (Exception e){
            throw new IllegalStateException(e.getMessage(),e);
        }
    }

}
