package blueeye.pojo.metric.system;

import blueeye.pojo.metric.MetricData;
import lombok.Data;

/**
 * @Author SDJin
 * @CreationDate 2023/6/21 16:23
 * @Description ：
 */
@Data
public class JvmThreadData extends MetricData {
    /**
     * 线程总数
     */
    private Integer total;
    /**
     * 可运行线程数
     */
    private Integer runnable;
    /**
     * 阻塞线程数
     */
    private Integer blocked;
    /**
     * 等待线程数
     */
    private Integer waiting;
    /**
     * 限时等待线程数
     */
    private Integer timeWaiting;
}
