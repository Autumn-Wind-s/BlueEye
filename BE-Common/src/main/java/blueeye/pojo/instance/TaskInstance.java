package blueeye.pojo.instance;

import blueeye.pojo.task.impl.TimerTask;
import lombok.Data;
import lombok.SneakyThrows;

import java.sql.Timestamp;
import java.util.concurrent.Callable;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 17:14
 * @Description ：
 */
@Data
public class TaskInstance implements Runnable{
    private final Integer instanceId;
    private final Integer taskId;
    private InstanceState state;
    private Integer executeCount;
    private Timestamp executionTime;
    private Timestamp finishTime;
    private String record="";
    private TimerTask task;
    public TaskInstance(Integer instanceId, Integer taskId) {
        this.instanceId = instanceId;
        this.taskId = taskId;
    }

    public TaskInstance(Integer instanceId, Integer taskId, InstanceState state,Integer executeCount, Timestamp executionTime,TimerTask task) {
        this.instanceId = instanceId;
        this.taskId = taskId;
        this.state = state;
        this.executeCount=executeCount;
        this.executionTime = executionTime;
        this.task=task;
    }

    public TaskInstance(Integer instanceId, Integer taskId, InstanceState state, Integer executeCount, Timestamp executionTime, Timestamp finishTime, String record) {
        this.instanceId = instanceId;
        this.taskId = taskId;
        this.state = state;
        this.executeCount = executeCount;
        this.executionTime = executionTime;
        this.finishTime = finishTime;
        this.record = record;
    }







    @SneakyThrows
    @Override
    public void run() {
        //执行实例所属任务的execute()
        String execute = task.execute(taskId,instanceId);
        //将返回值append到record中作为实例执行记录
        record+=execute;
        //设置完成时间
        finishTime=new Timestamp(System.currentTimeMillis());
        //设置对应任务为已完整执行过
        task.setIsExecuted(true);
    }
}
