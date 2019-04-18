package cn.qenan.fastrpc.common.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 字符串工具类
 *
 * @author luolei
 * @version 1.0
 * <p>
 * 12019/04/13
 */
public class StringUtil {

    public static boolean isEmpty(String s) {
        return StringUtils.isEmpty(s);
    }

    public static boolean isNotEmpty(String s) {
        return !StringUtils.isEmpty(s);
    }

    public static String[] split(String s, String operateChar) {
        return StringUtils.split(s, operateChar);
    }
}
