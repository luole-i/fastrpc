package cn.qenan.fastrpc.common.serialization;

import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 序列化接口
 *
 * @author luolei
 * @version 1.1
 * 2019/04/11
 */
public interface Serialization {

    <T> byte[] serialize(T obj);

    <T> T deserialize(byte[] data, Class<T> c);

}
