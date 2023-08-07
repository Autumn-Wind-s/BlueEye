package blueeye.pojo.po;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 16:31
 * @Description ：
 */
@Data
public class TaskPo {
    private Integer taskId;
    private String taskName;
    private String taskDescription;
    private Timestamp createTime;
    /**
     * 0为监控，1为接口，2为脚本
     */
    private Integer type;
    private Long cycle;
    private String preTask;
    private String isExecuted;
    private Integer order;
    private String json;

    public TaskPo(Integer taskId, String taskName, String taskDescription, Timestamp createTime, Integer type, Long cycle, String preTask, String isExecuted, Integer order) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.createTime = createTime;
        this.type = type;
        this.cycle = cycle;
        this.isExecuted = isExecuted;
        this.preTask = preTask;
        this.order = order;
    }


}
