package cn.qenan.fastrpc.common.exception;

/**
 * 服务异常状态码
 */
public enum ServerExceptionStat {
    /**
     * 服务端拒绝连接，未通过验证和限流
     */
    REFUSE,

    /**
     * 服务端调用执行错误
     */
    EXERROR
}
