package blueeye.pojo.metric;


import blueeye.pojo.alert.AlertRule;
import blueeye.pojo.task.impl.alert.AlertTask;
import lombok.Data;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 17:21
 * @Description ：
 */
@Data
public abstract class MetricData  {
   protected Integer id;
   protected Integer taskId;
   protected Integer instanceId;
   protected Timestamp createTime;


}
