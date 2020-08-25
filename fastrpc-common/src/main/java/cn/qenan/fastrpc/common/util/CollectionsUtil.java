package cn.qenan.fastrpc.common.util;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * 集合工具类
 *
 * @author luolei
 * @version 1.0
 * <p>
 * 2019/04/13
 */
public class CollectionsUtil {

    public static boolean isEmpty(Collection<?> collection) {
        return CollectionUtils.isEmpty(collection);
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !CollectionUtils.isEmpty(collection);
    }

    public static ArrayList<?> copyArrayList(Collection<?> collection) {
        return (ArrayList<?>) ((ArrayList) collection).clone();
    }
}
