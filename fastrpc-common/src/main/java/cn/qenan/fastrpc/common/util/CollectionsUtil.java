package cn.qenan.fastrpc.common.util;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;

/**
 * 集合工具类
 *
 * @author luolei
 * @version 1.0
 *
 * 2019/04/13
 */
public class CollectionsUtil {

    public static boolean isEmpty(Collection<?> collection){
        return CollectionUtils.isEmpty(collection);
    }

    public static boolean isNotEmpty(Collection<?> collection){
        return !CollectionUtils.isEmpty(collection);
    }
}
