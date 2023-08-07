package blueeye.pojo.metric.intf;

import blueeye.pojo.metric.MetricData;
import lombok.Data;

/**
 * @Author SDJin
 * @CreationDate 2023/2/6 20:54
 * @Description ：
 */
@Data
public class InterfaceData extends MetricData {
    /**
     * 接口请求总次数
     */
    private Long totalCount;
    /**
     * 接口请求成功次数
     */
    private Long successCount;
    /**
     * 接口请求失败次数
     */
    private Long failCount;
    /**
     * 所有请求平均耗时
     */
    private Long averageComputingTime;
    /**
     * 最大耗时
     */
    private Long maxComputingTime;
    /**
     * 最小耗时
     */
    private Long minComputingTime;

}
