package cn.qenan.fastrpc.common.serialization.fastjson;

import cn.qenan.fastrpc.common.serialization.Serialization;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;


public class SerializeFastjson implements Serialization {
    public <T> byte[] serialize(T obj) {
        return JSON.toJSONBytes(obj, SerializerFeature.SortField);
    }

    public <T> T deserialize(byte[] data, Class<T> c) {
        return JSON.parseObject(data,c, Feature.SortFeidFastMatch);
    }
}
