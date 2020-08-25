package cn.qenan.fastrpc.common.serialization;

import cn.qenan.fastrpc.common.properties.ClientProperties;
import cn.qenan.fastrpc.common.properties.FastRpcConfigurer;
import cn.qenan.fastrpc.common.properties.ServerProperties;
import cn.qenan.fastrpc.common.serialization.*;

/**
 * 序列化工具(1.0版本 基于Protostuff实现，后续实现多版本)
 *
 * @author luolei
 * @version 1.0
 * 2019/04/09
 */
public enum SerializationUtil {
    CLIENT(FastRpcConfigurer.getProperty(ClientProperties.SERIALIZE_TYPE)), SERVER(FastRpcConfigurer.getProperty(ServerProperties.SERIALIZE_TYPE));

    private Serialization serialization;

    SerializationUtil(String type) {
        this.serialization = AcquireSerialize.getSerialize(type);
    }

    /**
     * 序列化
     */
    public <T> byte[] serialize(T o) {
        return serialization.serialize(o);
    }

    /**
     * 反序列化
     */
    public <T> T deserialize(byte[] data, Class<?> c) {
        return (T) serialization.deserialize(data, c);
    }

    /**
     * 获取序列化内部类，默认protostuff
     */
    private static class AcquireSerialize {
        private final static String FASTJSON = "fastjson";
        private final static String AVRO = "avro";
        private final static String PROTOSTUFF = "protostuff";
        private final static String HESSIAN = "hessian";

        private static Serialization getSerialize(String type) {
            if(type == null){
                type = "protostuff";
            }
            Serialization serialization;
            switch (type) {
                case FASTJSON:
                    serialization = new FastJsonSerializer();
                    break;
                case AVRO:
                    serialization = new AvroSerializer();
                    break;
                case HESSIAN:
                    serialization = new HessianSerializer();
                    break;
                default:
                    serialization = new ProtostuffSerializer();
            }
            return serialization;
        }
    }
}
