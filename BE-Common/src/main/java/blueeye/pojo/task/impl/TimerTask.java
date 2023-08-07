package blueeye.pojo.task.impl;

import blueeye.pojo.po.TaskPo;
import blueeye.pojo.task.Task;
import lombok.Data;


import java.util.List;

/**
 * @Author SDJin
 * @CreationDate 2023/2/7 20:27
 * @Description ：调度任务
 */
@Data
public abstract class TimerTask extends Task  {
    /**
     * 实现类用Collections的加锁List，避免线程安全问题
     */
    protected List<Integer> preTask;
    protected Long cycle;
    protected Boolean isExecuted;
    /**
     * * 任务执行体,返回值为任务执行记录,注意抓取异常，保证即使发生异常，在finally块中能也将异常信息记录到执行记录中并不完整的记录返回
     * @return
     * @param  taskId 任务id ,instanceId 实例id
     * @throws Exception 执行过程中的各种异常，向上抛出，线程池的各种线程会抓取
     */
  public abstract String execute(int taskId,int instanceId) throws Exception;
}
