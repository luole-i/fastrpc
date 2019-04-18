package cn.luolei.rpc.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Bootstrap {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring.xml");
    }
}
