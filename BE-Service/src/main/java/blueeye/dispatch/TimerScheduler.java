package blueeye.dispatch;


import blueeye.context.BlueEyeContext;
import blueeye.pojo.instance.InstanceState;
import blueeye.pojo.instance.TaskInstance;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
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
     * 包括轮询delayQueue获取到期任务实例和轮询queue释放阻塞任务实例的两个守护线程
     */
    private ExecutorService bossThreadPool;

    /**
     * @param tickMs       时间轮中每格的时间跨度
     * @param wheelSize    每层时间轮的总格数
     * @param corePoolSize 实例执行器的核心线程数
     * @param retryNum     实例重试次数
     */
    public TimerScheduler(Optional<Long> tickMs, Optional<Integer> wheelSize, Optional<Integer> corePoolSize, Optional<Integer> retryNum) {
        //初始化各组件
        this.timeWheel = new TimeWheel(tickMs.orElse(20L), wheelSize.orElse(16), System.currentTimeMillis(), delayQueue);
        this.bossThreadPool = Executors.newFixedThreadPool(2);
        this.workerThreadPool = new TimerThreadPool(corePoolSize.orElse(Runtime.getRuntime().availableProcessors()), corePoolSize.orElse(Runtime.getRuntime().availableProcessors()), 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>(11, new Comparator<Runnable>() {
            @Override
            public int compare(Runnable o1, Runnable o2) {
                //先以时间排序，时间相同的以优先级排序，避免了任务饥饿问题
                int i = ((TaskInstance) o1).getExecutionTime().compareTo(((TaskInstance) o2).getExecutionTime());
                return i != 0 ? i : ((TaskInstance) o2).getTask().getOrder() - ((TaskInstance) o1).getTask().getOrder();
            }
        }), this, retryNum);
        // 一个线程定时轮询延迟队列取出推动一次时间轮运转
        this.bossThreadPool.submit(() -> {
            while (true) {
                this.advanceClock(tickMs.orElse(20L));
            }
        });
        //一个线程定时遍历阻塞的任务
        this.bossThreadPool.submit(() -> {
            while (true) {
                for (TaskInstance taskInstance : queue) {
                    //判断前置任务是否执行完成
                    if (BlueEyeContext.checkPre(taskInstance.getTaskId())) {
                        //更改状态为RUNNING
                        taskInstance.setState(InstanceState.RUNNING);
                        //重新执行任务
                        workerThreadPool.submit(taskInstance);
                        queue.remove(taskInstance);
                    }
                }
                Thread.sleep(tickMs.orElse(20L) * 2);
            }
        });
    }


    public void addTimerTaskEntry(TaskInstanceEntry entry) {
        if (!timeWheel.add(entry)) {
            // 任务已到期
            TaskInstance taskInstance = entry.getTaskInstance();
            if (taskInstance.getState().equals(InstanceState.READY)) {
                //更改状态，放入执行器中执行
                taskInstance.setState(InstanceState.RUNNING);
                workerThreadPool.execute(taskInstance);
            }

            if (taskInstance.getTask().getCycle() > 0) {
                TaskInstance instance = new TaskInstance(BlueEyeContext.dataCenter.getInstanceId().getAndIncrement(), taskInstance.getTaskId(), InstanceState.READY, 1, new Timestamp(taskInstance.getExecutionTime().getTime() + taskInstance.getTask().getCycle()), taskInstance.getTask());
                BlueEyeContext.dataCenter.getInstances().addInstance(instance);
                add(instance);
            }
        }
    }

    @Override
    public void add(TaskInstance taskInstance) {
//        log.info(new Timestamp(System.currentTimeMillis()) + "添加任务实例:" + taskInstance.getInstanceId());
        TaskInstanceEntry entry = new TaskInstanceEntry(taskInstance, taskInstance.getExecutionTime().getTime());
        addTimerTaskEntry(entry);
    }

    public void addBlockTask(TaskInstance taskInstance) {
        queue.add(taskInstance);
    }

    /**
     * 推动指针运转获取到期任务实例
     *
     * @param timeout 时间间隔
     * @return
     */
    @Override
    public synchronized void advanceClock(long timeout) {
        try {
            TaskInstanceList bucket = delayQueue.poll(timeout, TimeUnit.MILLISECONDS);
            if (bucket != null) {
                // 有任务到期，推进时间轮的指针
                timeWheel.advanceLock(bucket.getExpiration());
                // 执行到期任务(包含降级)
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
