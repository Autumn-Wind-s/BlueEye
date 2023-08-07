package blueeye.pojo.metric.executor;


import blueeye.pojo.metric.MetricData;
import lombok.Data;

/**
 * @Author SDJin
 * @CreationDate 2023/2/6 20:50
 * @Description ：
 */
@Data
public class ExecutorData extends MetricData {
    /**
     * 当前使用率
     */
    private Double currentUsage;
    /**
     * 峰值使用率
     */
    private Double peakUsage;
    /**
     * 核心线程数
     */
    private Integer corePoolSize;
    /**
     * 最大线程数
     */
    private Integer maxPoolSize;
    /**
     * 当前线程数
     */
    private Integer currentCount;
    /**
     * 活跃线程数
     */
    private Integer activeCount;
    /**
     * 最大出现的线程数
     */
    private Integer largestCount;
    /**
     * 阻塞队列容量
     */
    private Integer  blockQueueSize;
    /**
     * 阻塞队列已占用容量
     */
    private Integer queueOccupiedSize;
    /**
     * 阻塞队列剩余容量
     */
    private Integer queueSurplusSize;
    /**
     * 已完成的任务数
     */
    private Long completedTaskCount;

}
