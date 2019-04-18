package cn.qenan.fastrpc.common.serialization;

import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 序列化抽象类，提供一个模板
 *
 * @author luolei
 * @version 1.0
 * 2019/04/11
 */
public abstract class Serialization {
    protected static ConcurrentHashMap<Class<?>, Schema<?>> cache = new ConcurrentHashMap<Class<?>, Schema<?>>();

    protected static Objenesis objenesis = new ObjenesisStd(true);

    public abstract <T> byte[] serialize(T obj);

    public abstract <T> T deserialoze(byte[] data, Class<T> c);

    protected static <T> Schema<T> getSchema(Class<T> c) {
        Schema<T> schema = (Schema<T>) cache.get(c);
        if(schema==null){
            schema = RuntimeSchema.createFrom(c);
            cache.put(c,schema);
        }
        return schema;
    }
}
