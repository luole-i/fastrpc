package cn.luolei.rpc.server;

import cn.luolei.rpc.api.HelloService;
import cn.qenan.fastrpc.server.annotation.FastRpcService;

@FastRpcService(value = HelloService.class)
public class HelloServiceImpl implements HelloService {

    public String hello(String name) {
        return "reques succeed: hello "+name;
    }
}
