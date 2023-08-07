package blueeye.pojo.metric.system;

import blueeye.pojo.metric.MetricData;
import lombok.Data;

/**
 * @Author SDJin
 * @CreationDate 2023/6/21 16:12
 * @Description ：
 */
@Data
public class HeapData extends MetricData {
    /**
     *  堆内存最大容量，单位KB
     */
    private Long max;
    /**
     * 堆内存当前总容量，单位KB
     */
    private Long total;
    /**
     * 堆内存当前使用量，单位KB
     */
    private Long usage;
    /**
     * 堆内存中老年代当前容量，单位KB
     */
    private Long old;
    /**
     * 堆内存中年轻代survivor0区当前容量，单位KB
     */
    private Long survivor0;
    /**
     * 堆内存中年轻代survivor1区当前容量，单位KB
     */
    private Long survivor1;
    /**
     * 堆内存中年轻代eden区当前容量，单位KB
     */
    private Long eden;

    /**
     * 元空间当前容量
     */
    private Long metSpace;
}
