package cn.qenan.fastrpc.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC服务注解，用来标识一个类是服务实现类
 *
 * @author luolei
 * @version 1.0
 * 2019/04/09
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FastRpcService {

    /**
     * 服务的接口
     */
    Class<?> value();
    /**
     * 本版号
     *
     * @return
     */
    String version() default "";
}
