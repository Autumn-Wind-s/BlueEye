package blueeye.pojo.metric.system;

import blueeye.pojo.metric.MetricData;
import lombok.Data;

/**
 * @Author SDJin
 * @CreationDate 2023/6/21 15:28
 * @Description ：
 */
@Data
public class MemoryData extends MetricData {
    /**
     * 总量，单位MB
     */
    private Long total;
    /**
     * 使用量，单位MB
     */
    private Long usage;
    /**
     * 使用率，%
     */
    private Double usageRate;
}
