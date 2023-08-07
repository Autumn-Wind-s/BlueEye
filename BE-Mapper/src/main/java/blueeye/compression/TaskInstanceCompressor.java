package blueeye.compression;

import blueeye.pojo.instance.TaskInstance;
import blueeye.pojo.po.TaskInstancePo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author SDJin
 * @CreationDate 2023/6/19 20:46
 * @Description ：
 */
public class TaskInstanceCompressor {
    public static TaskInstancePo compress(TaskInstance instance) {
        return new TaskInstancePo(instance.getInstanceId(), instance.getTaskId(), String.valueOf(instance.getState()), instance.getExecuteCount(), instance.getExecutionTime(), instance.getFinishTime(),instance.getRecord());
    }

    public static List<TaskInstancePo> compress(List<TaskInstance> instances) {
        List<TaskInstancePo> taskInstancePos = new ArrayList<>();
        for (TaskInstance instance : instances) {
            taskInstancePos.add(compress(instance));
        }
        return taskInstancePos;
    }
}
