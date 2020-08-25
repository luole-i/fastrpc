package cn.qenan.fastrpc.client.failhand;

import cn.qenan.fastrpc.client.FastRpcRemote;
import cn.qenan.fastrpc.common.beans.FastRpcRequest;
import cn.qenan.fastrpc.common.exception.ServerException;
import cn.qenan.fastrpc.common.exception.NullAddressException;
import cn.qenan.fastrpc.common.exception.NullServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;

public class ExceptionHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

    public static void handler(Exception e){
        try {
            handler(e,null,null);
        } catch (Exception e1) {
            //只记录不处理，调用者自行处理
        }
    }
    public static Object handler(Exception e, FastRpcRequest request, String serviceAddress){
        if (e instanceof NullAddressException) {
            FailFast.execute(e);
        } else if (e instanceof NullServiceException) {
            FailFast.execute(e);
        } else if (e instanceof ConnectException) {
            return FailOver.execute(e,request,serviceAddress);
        }
        if (e instanceof ServerException) {
            return null;
        }
        return null;
    }
}
