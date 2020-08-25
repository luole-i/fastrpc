package cn.qenan.fastrpc.common.exception;

/**
 * 空地址异常
 */
public class NullAddressException extends RuntimeException{

    private static final long serialVersionUID = 1849001875164111889L;

    public NullAddressException() {
        super();
    }

    public NullAddressException(String message) {
        super(message);
    }

    public NullAddressException(String message,Throwable throwable){
        super(message,throwable);
    }

    public NullAddressException(Throwable throwable){
        super(throwable);
    }
}
