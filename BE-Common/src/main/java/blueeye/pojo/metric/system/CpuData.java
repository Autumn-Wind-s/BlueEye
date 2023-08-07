package blueeye.pojo.metric.system;

import blueeye.pojo.metric.MetricData;
import lombok.Data;

/**
 * @Author SDJin
 * @CreationDate 2023/6/21 15:24
 * @Description ：
 */
@Data
public class CpuData extends MetricData {
    /**
     * 使用率,%
     */
    private Double usageRate;
    /**
     * 温度,单位℃
     */
    private Double temperature ;
}
