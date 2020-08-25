package cn.qenan.fastrpc.common.exception;

/**
 * 空服务异常
 */
public class NullServiceException extends RuntimeException {

    private static final long serialVersionUID = 8664441644592472901L;

    public NullServiceException() {
        super();
    }

    public NullServiceException(String message) {
        super(message);
    }

    public NullServiceException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public NullServiceException(Throwable throwable) {
        super(throwable);
    }
}
