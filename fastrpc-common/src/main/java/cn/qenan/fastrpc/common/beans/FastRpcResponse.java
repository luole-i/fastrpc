package cn.qenan.fastrpc.common.beans;

/**
 * 封装rpc响应结果
 *
 * @author luolei
 * @version 1.0
 * 2019/04/09
 */
public class FastRpcResponse {

    private String requestId;
    private Exception exception;
    private Object result;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
