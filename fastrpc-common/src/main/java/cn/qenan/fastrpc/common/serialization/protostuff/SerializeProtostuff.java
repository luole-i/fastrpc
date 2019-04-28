package cn.qenan.fastrpc.common.serialization.protostuff;

import cn.qenan.fastrpc.common.serialization.Serialization;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.concurrent.ConcurrentHashMap;


public class SerializeProtostuff implements Serialization {

    private static Objenesis objenesis = new ObjenesisStd(true);

    private static ConcurrentHashMap<Class<?>, Schema<?>> cache = new ConcurrentHashMap<Class<?>, Schema<?>>();

    public <T> byte[] serialize(T obj) {
        Class<T> c = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(c);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    public <T> T deserialize(byte[] data, Class<T> c) {
        try {
            T message = (T) objenesis.newInstance(c);
            Schema<T> schema = (Schema<T>) getSchema(c);
            ProtostuffIOUtil.mergeFrom(data, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private static <T> Schema<T> getSchema(Class<T> c) {
        Schema<T> schema = (Schema<T>) cache.get(c);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(c);
            cache.put(c, schema);
        }
        return schema;
    }
}
