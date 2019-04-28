package cn.luolei.rpc.client;

import cn.luolei.rpc.api.HelloService;
import cn.luolei.rpc.api.Person;
import cn.qenan.fastrpc.client.FastRpcRemote;
import cn.qenan.fastrpc.client.cache.AbstractServiceAddressCache;
import cn.qenan.fastrpc.client.cache.DefaultServiceAddressCache;
import cn.qenan.fastrpc.client.cache.ServiceAddressCache;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;


public class HelloClient {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
     /*   ServiceAddressCache def =  DefaultServiceAddressCache.getSingletonCache();
        List<String> list = def.acquireServiceList(HelloService.class.getName(),"1.0");
        for(String s:list){
            System.out.println(s);
        }*/
        FastRpcRemote fastRpcRemote = context.getBean(FastRpcRemote.class);
        long star = System.currentTimeMillis();
        HelloService helloService = fastRpcRemote.create(HelloService.class,"1.0");
        Person person = new Person();
        person.setFirstName("luo");
        person.setLastName("lei");
        System.out.println(helloService.hello(person));
        System.out.println(System.currentTimeMillis() - star);
    }
}
