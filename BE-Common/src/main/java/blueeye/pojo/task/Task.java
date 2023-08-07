package blueeye.pojo.task;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 16:07
 * @Description ：
 */
@Data
public class Task {
    protected String taskName;
    protected Integer taskId;
    protected Timestamp createTime;
    protected String taskDescription;
    protected Integer order;
}
