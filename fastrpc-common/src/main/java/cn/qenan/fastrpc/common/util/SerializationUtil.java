package cn.qenan.fastrpc.common.util;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 序列化工具(1.0版本 基于Protostuff实现，后续实现多版本)
 *
 * @author luolei
 * @version 1.0
 * 2019/04/09
 */
public class SerializationUtil {

    private static ConcurrentHashMap<Class<?>, Schema<?>> cache = new ConcurrentHashMap<Class<?>, Schema<?>>();

    private static Objenesis objenesis = new ObjenesisStd(true);

    private SerializationUtil(){}

    /**
     * 序列化
     */
    public static <T> byte[] serialize(T o){
        Class<T> c = (Class<T>)o.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try{
            Schema<T> schema = getSchema(c);
            return ProtostuffIOUtil.toByteArray(o,schema,buffer);
        }catch (Exception e){
            throw new IllegalStateException(e.getMessage(),e);
        }finally {
            buffer.clear();
        }
    }

    /**
     *反序列化
     */
    public static <T>T deserialize(byte[] data,Class<?> c){
        try {
            T message = (T) objenesis.newInstance(c);
            Schema<T> schema = (Schema<T>) getSchema(c);
            ProtostuffIOUtil.mergeFrom(data,message,schema);
            return message;
        }catch (Exception e){
            throw new IllegalStateException(e.getMessage(),e);
        }
    }

    private static <T> Schema<T> getSchema(Class<T> c) {
        Schema<T> schema = (Schema<T>) cache.get(c);
        if(schema==null){
            schema = RuntimeSchema.createFrom(c);
            cache.put(c,schema);
        }
        return schema;
    }
}
