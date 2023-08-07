package blueeye.pojo.po;

import blueeye.pojo.alert.AlertMethod;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 17:25
 * @Description ：
 */
@Data
public class AlertRecordPo {
    private Integer recordId;
    private Integer taskId;
    private Timestamp alertTime;
    private String content;
    private String notifier;
    private String alertMethod;

    public AlertRecordPo() {
    }

    public AlertRecordPo(Integer recordId, Integer taskId, Timestamp alertTime, String content, String notifier, String alertMethod) {
        this.recordId = recordId;
        this.taskId = taskId;
        this.alertTime = alertTime;
        this.content = content;
        this.notifier = notifier;
        this.alertMethod = alertMethod;
    }
}
