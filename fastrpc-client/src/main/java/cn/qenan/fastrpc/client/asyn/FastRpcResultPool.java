package cn.qenan.fastrpc.client.asyn;

import cn.qenan.fastrpc.common.beans.FastRpcResponse;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class FastRpcResultPool {
    private static final Map<String, ArrayBlockingQueue<FastRpcResponse>> responseMap = new ConcurrentHashMap<>();

    public static void init(String key) {
        ArrayBlockingQueue<FastRpcResponse> arrayBlockingQueue = new ArrayBlockingQueue<>(1);
        responseMap.put(key, arrayBlockingQueue);
    }

    public static void putResult(FastRpcResponse response) {
        ArrayBlockingQueue<FastRpcResponse> arrayBlockingQueue = responseMap.get(response.getRequestId());
        try {
            arrayBlockingQueue.put(response);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        responseMap.put(response.getRequestId(), arrayBlockingQueue);
    }

    public static FastRpcResponse getResult(String key) throws InterruptedException {
        try {
            return responseMap.get(key).poll(1000, TimeUnit.MILLISECONDS);
        } finally {
            responseMap.remove(key);
        }
    }
}
