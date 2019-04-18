package cn.qenan.fastrpc.registry.zk;

import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml"})
public class ZKServiceRegistryTest {

    @Autowired
    ZKServiceRegistry zkServiceRegistry;
    @Autowired
    ZKServiceDiscovery zkServiceDiscovery;

    @Test
    public void register() {
        String address = "127.0.0.1:8080";
        String address1 = "127.0.0.1:8081";
        List<String> list = new ArrayList<>();
        list.add(address);
        list.add(address1);
        zkServiceRegistry.register("test", address);
        zkServiceRegistry.register("test", address1);
        String s = zkServiceDiscovery.discover("test");
        assertThat(list,hasItem(s));
    }
}