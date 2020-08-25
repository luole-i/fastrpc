package cn.qenan.fastrpc.common.util;

import cn.qenan.fastrpc.common.beans.FastRpcRequest;


import java.lang.reflect.Method;
import java.util.UUID;

public class ProtocolUtil {
    public static FastRpcRequest requestConvertToProtocol(String serviceName, String serviceVersion, Method method,Object[] objects){
        FastRpcRequest request = new FastRpcRequest();
        request.setRequstId(UUID.randomUUID().toString());
        request.setServiceName(serviceName);
        request.setInterfaceName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setServiceVersion(serviceVersion);
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(objects);
        return request;
    }
}
