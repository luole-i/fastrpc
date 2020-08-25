package cn.qenan.fastrpc.common.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 获取配置类
 *
 * @author luolei
 * @version 1.0
 * <p>
 * 2019/04/19
 */
public class FastRpcConfigurer{
    private final static Logger LOGGER = LoggerFactory.getLogger(FastRpcConfigurer.class);

    private static Map<String, String> propertyMap;

    static {
        Properties properties = new Properties();
        String path = Thread.currentThread().getContextClassLoader().getResource("").toString()+"fastrpc.properties";
        path = path.substring(6);
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(path));
            properties.load(inputStream);
            propertyMap = new HashMap<String, String>();
            for (Object proper : properties.keySet()) {
                String key = proper.toString();
                String value = properties.getProperty(key);
                propertyMap.put(key.trim(), value.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String name) {
        return propertyMap.get(name);
    }

    public static boolean isContains(String name) {
        return propertyMap.containsKey(name);
    }
}
