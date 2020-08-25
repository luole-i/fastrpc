package cn.qenan.fastrpc.common.exception;

public class ServerException extends RuntimeException {
    private static final long serialVersionUID = 1536747496211173032L;

    private ServerExceptionStat stat;

    public ServerExceptionStat getStat() {
        return stat;
    }

    public ServerException() {
        super();
    }

    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, ServerExceptionStat stat) {
        super(message);
        this.stat = stat;
    }

    public ServerException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ServerException(Throwable throwable) {
        super(throwable);
    }

    public ServerException(Throwable throwable,ServerExceptionStat stat) {
        super(throwable);
        this.stat = stat;
    }
}
