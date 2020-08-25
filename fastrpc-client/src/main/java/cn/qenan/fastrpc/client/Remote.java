package cn.qenan.fastrpc.client;

public interface Remote {
    /**
     * 创建一个不带版本的接口远程调用
     *
     * @param interfaceClass 接口Class
     * @param <T>
     * @return 返回接口代理类
     */
     <T> T create(final Class<?> interfaceClass);

    /**
     * 创建一个带版本的接口远程调用
     *
     * @param interfaceClass 接口Class
     * @param serviceVersion 版本号
     * @param <T>
     * @return 返回接口代理类
     */
     <T> T create(final Class<?> interfaceClass, final String serviceVersion);

    /**
     * 创建一个不带版本的接口远程调用，但是调用的服务名是我们自己命名的，而不是默认的接口名
     *
     * @param interfaceClass
     * @param serviceName
     * @param <T>
     * @return 返回接口代理类
     */
     <T> T createServiceName(final Class<?> interfaceClass, final String serviceName);

    /**
     * 创建一个带版本的接口远程调用，但是调用的服务名是我们自己命名的，而不是默认的接口名
     *
     * @param interfaceClass
     * @param serviceName
     * @param <T>
     * @return 返回接口代理类
     */
     <T> T create(final Class<?> interfaceClass, final String serviceName, final String serviceVersion);

}
