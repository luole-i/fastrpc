package cn.qenan.fastrpc.common.util;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * 查找带标签的类
 */
public class AnnotationUtil {
    private final static List<String> classNameList;

    static {
        classNameList = PackageUtil.getClassNameList("");
    }

    public static List<Object> getBeanWithAnnotation(Class<?>... annotationClasses) {
        List<Object> list = new ArrayList<>();
        for (String className : classNameList) {
            try {
                Class cls = Class.forName(className);
                boolean flg = false;
                for (Class<?> annotationClass : annotationClasses) {
                    if(flg){
                        break;
                    }
                    Annotation annotation = cls.getAnnotation(annotationClass);
                    if (annotation != null) {
                        Object o = cls.newInstance();
                        list.add(o);
                        flg = true;
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

}
