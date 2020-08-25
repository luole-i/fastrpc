package cn.qenan.fastrpc.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FastRpcServiceName {
    /**
     * 服务名
     */
    String serviceName();
    /**
     * 版本号
     */
    String version() default "";
}
