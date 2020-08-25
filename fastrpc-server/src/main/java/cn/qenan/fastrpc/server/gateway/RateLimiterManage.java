package cn.qenan.fastrpc.server.gateway;

import cn.qenan.fastrpc.common.properties.FastRpcConfigurer;
import cn.qenan.fastrpc.common.properties.ServerProperties;
import cn.qenan.fastrpc.common.util.IntegerUtil;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RateLimiterManage {
    private String qps = FastRpcConfigurer.getProperty(ServerProperties.QPS);

    private volatile static RateLimiterManage rateLimiterManage;

    private String waitingTime = FastRpcConfigurer.getProperty(ServerProperties.WAIRINGTIME);

    /**
     * 启动一个定时线程往令牌桶里面添加令牌
     */
    private ScheduledExecutorService scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);

    /**
     * 往桶里面添加令牌的时间间隔，默认1ms
     */
    private final int interval;

    /**
     * 每次添加令牌的个数
     */
    private final int produceTokenNumber;

    private RateLimiter rateLimiter;

    {
        if (IntegerUtil.isValidPositiveInt(qps)) {
            if (IntegerUtil.isValidPositiveInt(waitingTime))
                rateLimiter = new RateLimiter(Integer.valueOf(qps), Integer.valueOf(waitingTime));
            else
                rateLimiter = new RateLimiter(Integer.valueOf(qps));

        } else {
            throw new RuntimeException("configure error >>> qps format dont allow");
        }
        int qpsInt = Integer.valueOf(qps);
        if (qpsInt > 1000) {
            interval = 1;
            produceTokenNumber = qpsInt / 1000;
        } else {
            produceTokenNumber = 1;
            interval = 1000 / qpsInt;
        }
        scheduledThreadPoolExecutor.scheduleAtFixedRate(new produce(), 1000, interval, TimeUnit.MILLISECONDS);
    }

    public boolean acquire() {
        return rateLimiter.acquire();
    }

    /**
     * 添加令牌线程
     */
    private class produce implements Runnable {
        public void run() {
            rateLimiter.supplement(produceTokenNumber);
        }
    }

    public static RateLimiterManage getSingleRateLimiterManage(){
        if(rateLimiterManage == null){
            synchronized (RateLimiterManage.class){
                if(rateLimiterManage == null){
                    rateLimiterManage = new RateLimiterManage();
                }
            }
        }
        return rateLimiterManage;
    }
}
