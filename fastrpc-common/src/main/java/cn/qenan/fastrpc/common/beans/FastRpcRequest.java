package cn.qenan.fastrpc.common.beans;

/**
 * 封装rpc请求
 *
 * @author luolei
 * @version 1.0
 * 2019/04/09
 */
public class FastRpcRequest {
    private String requstId;
    private String interfaceName;
    private String serviceVersion;
    private String methodName;
    private Class<?>[] parameterTpyes;
    private Object[] parameters;

    public String getRequstId() {
        return requstId;
    }

    public void setRequstId(String requstId) {
        this.requstId = requstId;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTpyes() {
        return parameterTpyes;
    }

    public void setParameterTpyes(Class<?>[] parameterTpyes) {
        this.parameterTpyes = parameterTpyes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}
