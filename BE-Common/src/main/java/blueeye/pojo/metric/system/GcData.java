package blueeye.pojo.metric.system;

import blueeye.pojo.metric.MetricData;
import lombok.Data;

/**
 * @Author SDJin
 * @CreationDate 2023/6/21 15:43
 * @Description ：
 */
@Data
public class GcData extends MetricData {
    /**
     * yongGc总次数
     */
    private  Long yongGcFrequency;
    /**
     * yongGc总时间,单位s
     */
    private  Double yongGcTime;
    /**
     * fullGc总次数
     */
    private Long  fullGcFrequency;
    /**
     * fullGc总时间，单位s
     */
    private Double  fullGcTime;
}
