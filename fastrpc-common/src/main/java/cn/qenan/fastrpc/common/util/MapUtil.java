package cn.qenan.fastrpc.common.util;

import org.apache.commons.collections4.MapUtils;

import java.util.Map;

/**
 * map工具类
 *
 * @author luolei
 * @version 1.0
 * 2019/04/13
 */
public class MapUtil {

    public static boolean isEmpty(Map map){
        return MapUtils.isEmpty(map);
    }

    public static boolean isNotEmpty(Map map){
        return !MapUtils.isEmpty(map);
    }
}
