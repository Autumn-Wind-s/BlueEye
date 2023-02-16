package com.dispatch;

import com.pojo.instance.InstanceState;
import com.pojo.instance.TaskInstance;

import java.util.Optional;
import java.util.concurrent.*;

/**
 * @Author SDJin
 * @CreationDate 2023/2/5 21:45
 * @Description ：
 */
public class TimerThreadPool extends ThreadPoolExecutor {
    /**
     * 保持对调度器的引用，便于将阻塞任务放入执行器的队列中。
     */
    private TimerScheduler timerLauncher;
    /**
     * 实例重试阈值，可由用户配置，默认为3
     */
    private Integer num;

    public TimerThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, TimerScheduler timerLauncher, Optional<Integer> num) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.timerLauncher=timerLauncher;
        this.num=num.orElse(3);
    }

    public TimerThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public TimerThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public TimerThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }



    /**
     * 钩子方法，用于任务执行前的预处理，判断任务前置任务是否执行和当前任务状态
     *
     * @param t
     * @param r
     */
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        TaskInstance taskInstance = (TaskInstance) r;
        //首先判断前置任务是否已经执行完成
        if (!taskInstance.checkPre()) {
            //前置任务未执行完，抛出异常阻止任务的执行
            throw new RuntimeException("阻塞");
        }
        //判断是否处于首次人为终止，人为终止只会为改变任务的状态，其重试计数为默认值0，即使重复终止不会改变重试计数
        if (taskInstance.getState().equals(InstanceState.TERMINATED0)) {
                //实例首次运行时被人为终止，抛异常阻止任务执行
                throw new RuntimeException("终止");
        }
        //其他情况说明任务处以正常状态或允许次数范围的重试状态中，可以放行
    }

    /**
     * 钩子方法，用于任务执行完的后置处理
     *
     * @param r
     * @param t
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        TaskInstance taskInstance = (TaskInstance) r;
        if (t == null) {
            //任务正常执行完成，更改状态
            taskInstance.setState(InstanceState.FINISHED);
        } else {
            //任务未执行或未正常执行
            if ("阻塞".equals(t.getMessage())) {
                //任务阻塞，将任务状态更改为阻塞并放入任务执行器的阻塞队列中
                taskInstance.setState(InstanceState.BLOCKING);
                timerLauncher.add(taskInstance);
            } else if ("终止".equals(t.getMessage())) {
                //任务被首次人为终止，将任务的重试次数加1作为下次重试的次数
                taskInstance.setState(InstanceState.valueOf("TERMINATED1"));
            }  else {
                //任务属于执行时异常终止，报警通知用户发生异常终止

                // 判断是否是首次异常终止
                if (taskInstance.getState().name().indexOf("TERMINATED")!=-1) {
                    //首次终止，更改状态，将任务的重试次数加1作为下次重试的次数
                    taskInstance.setState(InstanceState.valueOf("TERMINATED1"));
                    //todo 报警
                } else {
                    //非首次终止，判断当前重试次数是否为最后一次，
                    String name = taskInstance.getState().name();
                    int n=name.charAt(name.length()-1)-'0';
                    if(n==num){
                        //最后一次重试仍然失败，更改状态为死亡，避免浪费资源
                        taskInstance.setState(InstanceState.DIE);
                    }else{
                        //仍可以继续重试，重试次数加1，报警
                        n++;
                        taskInstance.setState(InstanceState.valueOf("TERMINATED"+n));
                    }
                }
            }
        }
    }
}
