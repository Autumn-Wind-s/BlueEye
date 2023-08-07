package blueeye.pojo.metric.system;

import blueeye.pojo.metric.MetricData;
import lombok.Data;

/**
 * @Author SDJin
 * @CreationDate 2023/6/21 15:41
 * @Description ：
 */
@Data
public class HardDiskData extends MetricData {
    /**
     * 磁盘总量，单位GB
     */
    private Long total;
    /**
     * 磁盘使用量，单位GB
     */
    private Long usage;
    /**
     * 磁盘使用率，%
     */
    private Double usageRate;
}
