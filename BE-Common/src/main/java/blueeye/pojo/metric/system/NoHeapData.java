package blueeye.pojo.metric.system;

import blueeye.pojo.metric.MetricData;
import lombok.Data;

/**
 * @Author SDJin
 * @CreationDate 2023/6/21 16:18
 * @Description ：
 */
@Data
public class NoHeapData extends MetricData {
    /**
     * 初始字节数，单位KB
     */
    private Long init;
    /**
     * 最大字节数，单位KB
     */
    private Long max;
    /**
     * 已使用字节数，单位KB
     */
    private Long usage;
    /**
     * 可用字节数，单位KB
     */
    private Long usable;
}
