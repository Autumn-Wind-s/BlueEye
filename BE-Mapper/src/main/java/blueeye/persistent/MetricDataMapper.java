package blueeye.persistent;


import blueeye.pojo.po.MetricDataPo;

import java.util.List;

/**
 * @Author SDJin
 * @CreationDate 2023/2/6 15:55
 * @Description ：
 */
public abstract class MetricDataMapper {


    /**
     * 将传入的数据对象持久化到数据库
     *
     * @param metricDataPo
     * @return
     */
    public abstract boolean persistence(MetricDataPo metricDataPo);

    /**
     * 将传入的数据对象持久化到数据库
     * @param metricDataPo
     * @return
     */
    public abstract boolean persistence(List<MetricDataPo> metricDataPo);

    /**
     * 根据查询时间范围内任务数据
     *
     * @param taskId
     * @param startTime
     * @param endTime
     * @return
     */
   public abstract List<MetricDataPo> selectByTimeAndId(int taskId, long startTime, long endTime);

}
