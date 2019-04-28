package cn.qenan.fastrpc.common.properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

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
public class FastRpcConfigurer extends PropertyPlaceholderConfigurer {

    private static Map<String, String> propertyMap;

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        super.processProperties(beanFactoryToProcess, props);
        propertyMap = new HashMap<String, String>();
        for (Object proper : props.keySet()) {
            String key = proper.toString();
            String value = props.getProperty(key);
            propertyMap.put(key.trim(), value.trim());
        }
    }

    public static String getProperty(String name) {
        return propertyMap.get(name);
    }

    public static boolean isContains(String name) {
        return propertyMap.containsKey(name);
    }
}
