package com.dispatch;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.DelayQueue;

/**
 * @Author SDJin
 * @CreationDate 2023/1/30 22:17
 * @Description ：
 */
@Data
@Slf4j
public class TimeWheel {


    /**
     * 基本时间跨度
     */
    private long timeSpan;
    /**
     * 时间单位个数
     */
    private int wheelSize;
    /**
     * 总体时间跨度
     */
    private long interval;
    /**
     * 当前所处时间
     */
    private long currentTime;
    /**
     * 定时任务列表
     */
    private TaskInstanceList[] buckets;
    /**
     * 上层时间轮
     */
    private volatile TimeWheel overflowWheel;
    /**
     * 一个Timer只有一个DelayQueue,协助推进时间轮
     */
    private DelayQueue<TaskInstanceList> delayQueue;
    /**
     * 最大的wheelSize
     */
    private static final int MAXIMUM_WHEELSIZE = 1 << 30;


    public TimeWheel(long timeSpan, int wheelSize, long currentTime, DelayQueue<TaskInstanceList> delayQueue) {
        this.timeSpan = timeSpan;
        this.wheelSize = wheelSizeFor(wheelSize);
        this.interval = timeSpan * this.wheelSize;
        this.buckets = new TaskInstanceList[this.wheelSize];
        this.currentTime = currentTime - (currentTime % timeSpan);
        this.delayQueue = delayQueue;
        for (int i = 0; i < this.wheelSize; i++) {
            buckets[i] = new TaskInstanceList();
        }
    }

    /**
     * 处理wheelSize为2的次幂
     * @param wheelSize
     * @return
     */
    static final int wheelSizeFor(int wheelSize) {
        int n = wheelSize - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_WHEELSIZE) ? MAXIMUM_WHEELSIZE : n + 1;
    }

    public boolean add(TaskInstanceEntry entry) {
        long expiration = entry.getExpireMs();
        if (expiration < timeSpan + currentTime) {
            // 定时任务到期
            return false;
        } else if (expiration < currentTime + interval) {
            // 扔进当前时间轮的某个槽里,只有时间大于某个槽,才会放进去
            long virtualId = (expiration / timeSpan);
            //类似于HashMap的优化
            int index = (int) ((virtualId-1) & wheelSize);
            TaskInstanceList bucket = buckets[index];
            bucket.addTask(entry);
            // 设置bucket 过期时间
            if (bucket.setExpiration(virtualId * timeSpan)) {
                // 设好过期时间的bucket需要入队
                delayQueue.offer(bucket);
                return true;
            }
        } else {
            // 当前轮不能满足,需要扔到上一轮
            TimeWheel timeWheel = getOverflowWheel();
            return timeWheel.add(entry);
        }
        return false;
    }

    private TimeWheel getOverflowWheel() {
        if (overflowWheel == null) {
            synchronized (this) {
                if (overflowWheel == null) {
                    overflowWheel = new TimeWheel(interval, wheelSize, currentTime, delayQueue);
                }
            }
        }
        return overflowWheel;
    }

    /**
     * 推进指针
     *
     * @param timestamp
     */
    public void advanceLock(long timestamp) {
        if (timestamp > currentTime + timeSpan) {
            currentTime = timestamp - (timestamp % timeSpan);
            if (overflowWheel != null) {
                this.getOverflowWheel().advanceLock(timestamp);
            }
        }
    }


}
