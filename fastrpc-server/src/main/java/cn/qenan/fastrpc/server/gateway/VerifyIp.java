package cn.qenan.fastrpc.server.gateway;

import cn.qenan.fastrpc.common.properties.FastRpcConfigurer;
import cn.qenan.fastrpc.common.properties.ServerProperties;
import cn.qenan.fastrpc.common.util.StringUtil;

import java.util.Arrays;

/**
 * 验证客户端ip是否是允许ip
 */
public class VerifyIp {
    private static String[] allowableIpArray = StringUtil.split(FastRpcConfigurer.getProperty(ServerProperties.ALLOWABLE_IP), ";");

    static {
        Arrays.sort(allowableIpArray);
    }

    public static boolean isExist(String ip) {
        int index = Arrays.binarySearch(allowableIpArray, ip);
        if (index < 0) {
            return false;
        }
        return true;
    }
}
