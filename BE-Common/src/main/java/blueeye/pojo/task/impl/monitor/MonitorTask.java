package blueeye.pojo.task.impl.monitor;


import blueeye.pojo.po.TaskPo;
import blueeye.pojo.task.impl.TimerTask;

import lombok.Data;

/**
 * @Author SDJin
 * @CreationDate 2023/2/7 19:36
 * @Description ：监控任务的区别体现在监控逻辑，利用回调机制避免创建过多类，且保证的可扩展性
 */
@Data
public class MonitorTask extends TimerTask {
    private final ExecuteCallback monitorCallback;



    public MonitorTask(ExecuteCallback callback){
        monitorCallback=callback;
    }


    @Override
    public String execute(int taskId, int instanceId) throws Exception {
        return monitorCallback.execute(taskId,instanceId);
    }
}
