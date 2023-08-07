package blueeye.pojo.alert;


import lombok.Data;

import java.sql.Timestamp;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 17:25
 * @Description ：
 */
@Data
public class AlertRecord  {
    private final Integer recordId;
    private final Integer taskId;
    private Timestamp alertTime;
    private String content;
    private String notifier;
    private AlertMethod alertMethod;

    public AlertRecord(Integer recordId, Integer taskId, Timestamp alertTime, String content, String notifier, AlertMethod alertMethod) {
        this.recordId = recordId;
        this.taskId = taskId;
        this.alertTime = alertTime;
        this.content = content;
        this.notifier = notifier;
        this.alertMethod = alertMethod;
    }

    public AlertRecord(Integer recordId, Integer taskId) {
        this.recordId = recordId;
        this.taskId = taskId;
    }
}
