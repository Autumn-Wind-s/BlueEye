package blueeye.pojo.metric.sql;


import blueeye.pojo.metric.MetricData;
import lombok.Data;

/**
 * @Author SDJin
 * @CreationDate 2023/2/6 20:53
 * @Description ：
 */
@Data
public class SqlData extends MetricData {
    /**
     * sql语句
     */
    private String sql;
    /**
     * 执行总次数
     */
    private Long executeCount;
    /**
     * 平均执行耗时
     */
    private Long averageComputingTime;
    /**
     * 最近一次执行耗时
     */
    private Long RecentComputingTime;

}
