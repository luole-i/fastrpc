package cn.qenan.fastrpc.common.serialization.kryo;

import cn.qenan.fastrpc.common.serialization.Serialization;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SerializeKryo implements Serialization {
    public <T> byte[] serialize(T obj) {
        Kryo kryo = new Kryo();
        kryo.setReferences(false);
        kryo.register(obj.getClass(), new JavaSerializer());
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        Output output = new Output(byteOut);
        kryo.writeClassAndObject(output, obj);
        output.flush();
        output.close();
        byte[] data = byteOut.toByteArray();
        try {
            byteOut.flush();
            byteOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public <T> T deserialize(byte[] data, Class<T> c) {
        Kryo kryo = new Kryo();
        kryo.setReferences(false);
        kryo.register(c, new JavaSerializer());
        ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
        Input input = new Input(byteIn);
        return (T) kryo.readClassAndObject(input);
    }
}
