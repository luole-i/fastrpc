package cn.qenan.fastrpc.server.gateway;

import cn.qenan.fastrpc.common.util.UnsafeUtil;
import sun.misc.Unsafe;

/**
 * 令牌桶限流器
 */
public class RateLimiter {
    /**
     * 此时桶中还有多少令牌
     */
    private volatile long token;

    /**
     * 桶令牌容量
     */
    private final long capacityToken;

    /**
     * 获取令牌最长时间，超过时间没有获取则直接false,默认1s
     */
    private long waitingTime = 1000;

    /**
     * 用于原子更新
     */
    private static Unsafe unsafe;

    private static long valueOffset;

    static {
        try {
            unsafe = UnsafeUtil.getUnsafe();
            valueOffset = unsafe.objectFieldOffset(RateLimiter.class.getDeclaredField("token"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public RateLimiter(long token) {
        this.token = token;
        this.capacityToken = token;
    }

    public RateLimiter(long token, long waitingTime) {
        this.token = token;
        this.capacityToken = token;
        this.waitingTime = waitingTime;
    }

    /**
     * 获取一个令牌
     *
     * @return 获取是否成功
     */
    public boolean acquire() {
        return acquire(1);
    }

    /**
     * 获取tokenNumber个令牌
     *
     * @param tokenNumber 令牌数
     * @return 获取是否成功
     */
    public boolean acquire(int tokenNumber) {
        long current = token;
        if (current < tokenNumber) {
            current = capacityToken;
        }
        long expectTime = waitingTime;
        long endTime = System.currentTimeMillis() + waitingTime;
        while (current >= tokenNumber) {
            if (compareAndSet(current, current - tokenNumber)) {
                return true;
            }
            current = token;
            //当时间快结束时快速获取
            if (current < tokenNumber && expectTime > 100) {
                synchronized (this) {
                    try {
                        wait(expectTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                current = token;
                if (current < tokenNumber) {
                    current = capacityToken;
                }
                expectTime = endTime - System.currentTimeMillis();
            }
        }
        return false;
    }

    /**
     * 补充令牌数
     *
     * @param tokenNumber 补充个数
     */
    public synchronized void supplement(int tokenNumber) {
        if (tokenNumber + token > capacityToken) {
            token = capacityToken;
        } else {
            token = token + tokenNumber;
        }
        notifyAll();
    }

    public void supplement() {
        supplement(1);
    }

    /**
     * 原子更新token
     *
     * @param except 期待值
     * @param update 更新值
     * @return 更新是否成功
     */
    private boolean compareAndSet(long except, long update) {
        return unsafe.compareAndSwapLong(this, valueOffset, except, update);
    }
}
