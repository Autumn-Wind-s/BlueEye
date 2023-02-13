package com.mapper;

import com.pojo.po.MetricDataPo;

import java.util.List;

/**
 * @Author SDJin
 * @CreationDate 2023/2/6 15:55
 * @Description ：
 */
public interface MetricDataMapper {
    /**
     *将传入的数据对象持久化到数据库
     * @param metricDataPo
     * @return
     */
    boolean persistence(MetricDataPo metricDataPo);

    /**
     * 根据查询时间范围内任务数据
     * @param taskId
     * @param startTime
     * @param endTime
     * @return
     */
    List<MetricDataPo> selectByTimeAndId(int taskId,long startTime,long endTime);

}
