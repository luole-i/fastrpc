package cn.qenan.fastrpc.common.util;

/**
 * 整数工具类
 *
 * @author luolei
 * @version 1.0
 * <p>
 * 2019/04/21
 */
public class IntegerUtil {
    public static boolean isValidInt(String s) {
        if (StringUtil.isEmpty(s)) {
            return false;
        }
        boolean flag = true;
        int i = 0;
        if (s.charAt(0) == '-'||s.charAt(0)=='+') {
            i = 1;
        }
        for (; i < s.length(); i++) {
            if (s.charAt(i) < '0' || s.charAt(i) > '9') {
                flag = false;
                break;
            }
        }
        return flag;
    }

    public static boolean isValidPositiveInt(String s) {
        boolean flag = true;
        if (!isValidInt(s)) {
            return false;
        }
        int number = Integer.valueOf(s);
        if (number < 1) {
            flag = false;
        }
        return flag;
    }
}
