package cn.luolei.rpc.server;

import cn.qenan.fastrpc.server.FastRpcServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Bootstrap {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        try {
            FastRpcServer fastRpcServer = (FastRpcServer) context.getBean("fastRpcServer");
            fastRpcServer.start(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
