package blueeye.pojo.task.impl.alert;

import blueeye.pojo.alert.AlertMethod;
import blueeye.pojo.task.Task;
import lombok.Data;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 16:12
 * @Description ：
 */
@Data
public class AlertTask extends Task {
    private String notifier;
    private String content;
    private AlertMethod method;
}
