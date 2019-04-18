package cn.luolei.rpc.client;

import cn.luolei.rpc.api.HelloService;
import cn.qenan.fastrpc.client.Remote;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HelloClient {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        Remote remote = context.getBean(Remote.class);

        long star = System.currentTimeMillis();
        HelloService helloService = remote.create(HelloService.class);
        System.out.println(helloService.hello("word"));
        System.out.println(System.currentTimeMillis()-star);
    }
}
