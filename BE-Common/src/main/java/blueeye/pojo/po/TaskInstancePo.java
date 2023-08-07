package blueeye.pojo.po;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 17:24
 * @Description ：
 */
@Data
public class TaskInstancePo {
    private Integer instanceId;
    private Integer taskId;
    private String state;
    private Integer executeCount;
    private Timestamp executionTime;
    private Timestamp finishTime;
    private String record;

    public TaskInstancePo(Integer instanceId, Integer taskId, String state,Integer executeCount, Timestamp executionTime, Timestamp finishTime,  String record) {
        this.instanceId = instanceId;
        this.taskId = taskId;
        this.state = state;
        this.executeCount=executeCount;
        this.executionTime = executionTime;
        this.finishTime = finishTime;
        this.record = record;
    }
}
