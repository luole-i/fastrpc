package cn.luolei.rpc.client;

import cn.luolei.rpc.api.HelloService;
import cn.qenan.fastrpc.client.Remote;
import cn.qenan.fastrpc.client.asyn.RemoteProxy;

public class HelloClient {

    public static void main(String[] args) throws InterruptedException {
        Remote remote = RemoteProxy.getRemote();
        HelloService helloService = remote.create(HelloService.class);
        for (int i = 1; i <= 100; i++) {
            long start = System.currentTimeMillis();
            System.out.println(helloService.hello("luolei"));
            System.out.println(System.currentTimeMillis() - start);
        }
    }
}
