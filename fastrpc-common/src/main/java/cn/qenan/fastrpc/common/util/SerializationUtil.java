package cn.qenan.fastrpc.common.util;

import cn.qenan.fastrpc.common.properties.FastRpcConfigurer;
import cn.qenan.fastrpc.common.properties.FastRpcProperties;
import cn.qenan.fastrpc.common.serialization.Serialization;
import cn.qenan.fastrpc.common.serialization.fastjson.SerializeFastjson;
import cn.qenan.fastrpc.common.serialization.kryo.SerializeKryo;
import cn.qenan.fastrpc.common.serialization.protostuff.SerializeProtostuff;

/**
 * 序列化工具(1.0版本 基于Protostuff实现，后续实现多版本)
 *
 * @author luolei
 * @version 1.0
 * 2019/04/09
 */
public class SerializationUtil {

    private static Serialization serializeFastjson;

    private static String serializeType = FastRpcConfigurer.getProperty(FastRpcProperties.SERIALIZE_TYPE);


    static {
        serializeFastjson = AcquireSerialize.getSerialize(serializeType);
    }

    /**
     * 序列化
     */
    public static <T> byte[] serialize(T o) {
        return serializeFastjson.serialize(o);
    }

    /**
     * 反序列化
     */
    public static <T> T deserialize(byte[] data, Class<?> c) {
        return (T) serializeFastjson.deserialize(data, c);
    }

    /**
     * 获取序列化内部类，默认protostuff
     */
    private static class AcquireSerialize {
        private final static String FASTJSON = "fastjson";
        private final static String KRYO = "kryo";
        private final static String PROTOSTUFF = "protostuff";

        private static Serialization getSerialize(String type) {
            Serialization serialization = new SerializeProtostuff();
            if (FASTJSON.equals(type)) {
                serialization = new SerializeFastjson();
            }
            if (KRYO.equals(type)) {
                serialization = new SerializeKryo();
            }
            return serialization;
        }
    }

}
