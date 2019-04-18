package cn.luolei.rpc.server;

import cn.luolei.rpc.api.HelloService;
import cn.luolei.rpc.api.Person;
import cn.qenan.fastrpc.server.FastRpcService;

@FastRpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    public String hello(String name) {
        return "hello!"+name;
    }

    public String hello(Person person) {
        return null;
    }
}
