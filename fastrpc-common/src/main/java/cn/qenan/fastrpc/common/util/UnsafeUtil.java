package cn.qenan.fastrpc.common.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * 反射获取Java Unsafe工具类
 */
public class UnsafeUtil {
    private static Unsafe unsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(Unsafe.class);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static Unsafe getUnsafe(){
        return unsafe;
    }
}
