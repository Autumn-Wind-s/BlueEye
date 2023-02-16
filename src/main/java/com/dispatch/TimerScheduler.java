package com.dispatch;

import com.pojo.instance.InstanceState;
import com.pojo.instance.TaskInstance;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * @Author SDJin
 * @CreationDate 2023/1/30 22:20
 * @Description ：
 */
@Data
@Slf4j
public class TimerScheduler implements Timer {


    /**
     * 底层时间轮
     */
    private TimeWheel timeWheel;
    /**
     * 一个Timer只有一个延时队列，线程安全
     */
    private DelayQueue<TaskInstanceList> delayQueue = new DelayQueue<>();
    /**
     * 存放阻塞任务的队列,线程安全
     */
    private ConcurrentLinkedQueue<TaskInstance> queue = new ConcurrentLinkedQueue<>();
    /**
     * 实例执行器
     */
    private ExecutorService workerThreadPool;
    /**
     * 包括轮询delayQueue获取过期任务和轮询queue释放阻塞任务的两个工作线程
     */
    private ExecutorService bossThreadPool;


    public TimerScheduler(Optional<Integer> tickMs, Optional<Integer> wheelSize, Optional<Integer> corePoolSize, Optional<Integer> retryNum) {
        this.timeWheel = new TimeWheel(tickMs.orElse(20), wheelSize.orElse(16), System.currentTimeMillis(), delayQueue);
        this.bossThreadPool = Executors.newFixedThreadPool(2);
        this.workerThreadPool = new TimerThreadPool(corePoolSize.orElse(Runtime.getRuntime().availableProcessors()), corePoolSize.orElse(Runtime.getRuntime().availableProcessors()), 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>(11,new Comparator<Runnable>() {
            @Override
            public int compare(Runnable o1, Runnable o2) {
                return ((TaskInstance) o2).getOrder() - ((TaskInstance) o1).getOrder();
            }
        }), this, retryNum);
        // 定时推动一次时间轮运转
        this.bossThreadPool.submit(() -> {
            while (true) {
                this.advanceClock(tickMs.orElse(20));
            }
        });
        //定时遍历阻塞的任务
        this.bossThreadPool.submit(() ->{
            while(true){
                for (TaskInstance taskInstance : queue) {
                    //判断任务是否为终止
                    if(taskInstance.getState().equals(InstanceState.TERMINATED0)){
                        queue.remove(taskInstance);
                    }
                    //判断前置任务是否执行完成
                    if(true){
                        taskInstance.setState(InstanceState.RUNNING);
                        workerThreadPool.submit(taskInstance);
                        queue.remove(taskInstance);
                    }
                }
                Thread.sleep(tickMs.orElse(20)*2);
            }
        });
    }


    public void addTimerTaskEntry(TaskInstanceEntry entry) {
        if (!timeWheel.add(entry)) {
            // 任务已到期
            TaskInstance taskInstance = entry.getTaskInstance();
            log.info("=====任务:{} 已到期,准备执行============");
            workerThreadPool.submit(taskInstance);
        }
    }

    @Override
    public void add(TaskInstance taskInstance) {
        log.info("=======添加任务开始====task:{}");
        TaskInstanceEntry entry = new TaskInstanceEntry(taskInstance, taskInstance.getExecutionTime().getTime());
        addTimerTaskEntry(entry);
    }

    public void addBlockTask(TaskInstance taskInstance) {
        queue.add(taskInstance);
    }

    /**
     * 推动指针运转获取过期任务
     *
     * @param timeout 时间间隔
     * @return
     */
    @Override
    public synchronized void advanceClock(long timeout) {
        try {
            TaskInstanceList bucket = delayQueue.poll(timeout, TimeUnit.MILLISECONDS);
            if (bucket != null) {
                // 推进时间
                timeWheel.advanceLock(bucket.getExpiration());
                // 执行过期任务(包含降级)
                bucket.clear(this::addTimerTaskEntry);
            }
        } catch (InterruptedException e) {
            log.error("advanceClock error");
        }
    }

    @Override
    public int size() {
        return 10;
    }

    @Override
    public void shutdown() {
        this.bossThreadPool.shutdown();
        this.workerThreadPool.shutdown();
        this.timeWheel = null;
    }


}
