package cn.luolei.rpc.server;

import cn.luolei.rpc.api.bean.Person;
import cn.qenan.fastrpc.server.annotation.FastRpcServiceName;

@FastRpcServiceName(serviceName = "helloService")
public class HelloServiceName implements cn.luolei.rpc.api.HelloService {

    public String hello(String name) {
        return name+"nannan";
    }

    public String hello(Person person) {
        return null;
    }
}
