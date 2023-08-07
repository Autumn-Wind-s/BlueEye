package blueeye.pojo.metric.system;

import blueeye.pojo.metric.MetricData;
import lombok.Data;

/**
 * @Author SDJin
 * @CreationDate 2023/6/21 15:34
 * @Description ：
 */
@Data
public class LoadData extends MetricData {
    /**
     * 系统1分钟内负载
     */
    private Double oneMinute;
    /**
     * 系统5分钟内负载
     */
    private Double fiveMinute;
    /**
     * 系统15分钟内负载
     */
    private Double fifteenMinute;
}
