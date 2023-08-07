package blueeye.decompression;

import blueeye.pojo.instance.InstanceState;
import blueeye.pojo.instance.TaskInstance;
import blueeye.pojo.po.TaskInstancePo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author SDJin
 * @CreationDate 2023/6/19 20:47
 * @Description ：
 */
public class TaskInstanceDecompressor {
    public static TaskInstance decompress(TaskInstancePo instancePo){
        return new TaskInstance(instancePo.getInstanceId(), instancePo.getTaskId(), InstanceState.valueOf(instancePo.getState()),instancePo.getExecuteCount(),instancePo.getExecutionTime(),instancePo.getFinishTime(), instancePo.getRecord());
    }
    public static List<TaskInstance> decompress(List<TaskInstancePo> instancePo){
        List<TaskInstance> taskInstances = new ArrayList<>();
        for (TaskInstancePo taskInstancePo : instancePo) {
            taskInstances.add(decompress(taskInstancePo));
        }
        return taskInstances;
    }
}
