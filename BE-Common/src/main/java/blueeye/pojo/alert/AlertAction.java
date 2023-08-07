package blueeye.pojo.alert;

import lombok.Data;

/**
 * @Author SDJin
 * @CreationDate 2023/2/4 14:20
 * @Description ：
 */
@Data
public class AlertAction {
   AlertMethod method;
   String notifier;
   String content;
}
