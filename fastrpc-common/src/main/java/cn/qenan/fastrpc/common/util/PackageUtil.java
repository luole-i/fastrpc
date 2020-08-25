package cn.qenan.fastrpc.common.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 查找file包或jar包下面的类的全限定名
 */
public class PackageUtil {

    public static List<String> getClassNameList(String packageName) {
        return getClassNameList(packageName, true);
    }

    public static List<String> getClassNameList(String packageName, boolean childPackage) {
        packageName = packageName.replace(".", "/");
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(packageName);
        if (url.getProtocol().equals("jar")) {
            return getJarClassNameList(url, packageName, childPackage);
        }
        String path = url.getPath().substring(1);
        return getFilePackageName(path, packageName, childPackage);
    }

    private static List<String> getFilePackageName(String path, String packageName, boolean childPackage) {
        ArrayList<String> packageNameList = new ArrayList<String>();
        File file = new File(path);
        File[] childFiles = file.listFiles();
        if(!packageName.equals("")){
            packageName += "/";
        }
        for (File x : childFiles) {
            if(x.isDirectory()){
                if(childPackage){
                    packageNameList.addAll(getFilePackageName(path+"/"+x.getName(),packageName+x.getName(),childPackage));
                }
            }else if(x.getName().endsWith(".class")){
                String className = "";
                if(!packageName.equals("")){
                    className = packageName+x.getName();
                }else {
                    className = x.getName();
                }
                addClassName(packageNameList, className);
            }
        }
        return packageNameList;
    }

    private static List<String> getJarClassNameList(URL url, String packageName, boolean childPackage) {
        ArrayList<String> packageNameList = new ArrayList<String>();
        try {
            String path = url.getPath().substring(6);
            path = path.split("!")[0];
            JarFile jarFile = new JarFile(path);
            Enumeration<JarEntry> enumeration = jarFile.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = enumeration.nextElement();
                String name = jarEntry.getName();
                int lastIndex = name.lastIndexOf("/");
                if (name.endsWith(".class")) {
                    if (!childPackage) {
                        if (packageName.equals(name.substring(0, lastIndex))) {
                            addClassName(packageNameList, name);
                        }
                    } else {
                        addClassName(packageNameList, name);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return packageNameList;
    }

    private static void addClassName(ArrayList<String> packageNameList, String name) {
        name = name.replace("/", ".");
        int index = name.lastIndexOf(".");
        name = name.substring(0, index);
        packageNameList.add(name);
    }
}
