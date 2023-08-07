package blueeye.dispatch;

import blueeye.context.BlueEyeContext;
import blueeye.pojo.instance.InstanceState;
import blueeye.pojo.instance.TaskInstance;
import blueeye.pojo.task.impl.TimerTask;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * @Author SDJin
 * @CreationDate 2023/2/5 21:45
 * @Description ：
 */
@Slf4j
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
        //首先判断是否处于终止状态，是说明任务实例在提交到执行器后到执行前已被人为终止更改状态了
        if (taskInstance.getState().equals(InstanceState.TERMINATED)) {
            //实例被人为终止，抛异常阻止任务执行
            throw new RuntimeException("终止");
        }
        //判断前置任务是否已经执行完成，走到这一步说明任务状态不为终止，
        if (!BlueEyeContext.checkPre(taskInstance.getTaskId())) {
            //前置任务未执行完，抛出异常阻止任务的执行
            throw new RuntimeException("阻塞");
        }

        //其他情况说明任务处以正常运行状态，放行
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
            log.debug(taskInstance.getTask().getTaskName()+"任务实例"+taskInstance+"在"+taskInstance.getFinishTime()+"成功执行");
//            if (taskInstance.getCycle() > 0) {
//                TaskInstance instance = new TaskInstance(BlueEyeContext.dataCenter.getInstanceId().getAndIncrement(), taskInstance.getTaskId(), InstanceState.READY, 1, new Timestamp(taskInstance.getFinishTime().getTime() + taskInstance.getCycle()), taskInstance.getCycle(), taskInstance.getOrder(), taskInstance.getTask());
//                BlueEyeContext.dataCenter.getInstances().addInstance(instance);
//                timerLauncher.add(instance);
//            }
        } else {
            log.error("实例"+taskInstance.getInstanceId()+"执行发生错误");
            //任务未执行或未正常执行
            if ("阻塞".equals(t.getMessage())) {
                //任务阻塞，将任务状态更改为阻塞并放入任务执行器的阻塞队列中
                taskInstance.setState(InstanceState.BLOCKING);
                timerLauncher.add(taskInstance);
            } else if ("终止".equals(t.getMessage())) {
                //任务在执行前被人为终止，无需其他操作
            }  else {
                //任务属于执行时异常终止，更改任务状态并将异常信息记录到实例执行记录中
                taskInstance.setState(InstanceState.TERMINATED);
                taskInstance.setRecord(taskInstance.getRecord()+"第"+taskInstance.getExecuteCount()+"次执行任务实例时发生错误导致终止，\n执行时间："+System.currentTimeMillis()+"\n错误信息："+t.getMessage());
                //todo 报警通知管理员上线处理终止任务
            }
        }
    }

}
