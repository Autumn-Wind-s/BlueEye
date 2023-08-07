package blueeye.pojo.po;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 17:24
 * @Description ：
 */
@Data
public class MetricDataPo {
    private Integer id;
    private Integer taskId;
    private Integer instanceId;
    private Timestamp createTime;
    private Integer dataType;
    private String json;

    public MetricDataPo() {
    }

    public MetricDataPo(Integer id, Integer taskId, Integer instanceId, Timestamp createTime, Integer dataType) {
        this.id = id;
        this.taskId = taskId;
        this.instanceId = instanceId;
        this.createTime = createTime;
        this.dataType = dataType;
    }
}
