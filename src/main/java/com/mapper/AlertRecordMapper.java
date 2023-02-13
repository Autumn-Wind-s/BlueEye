package com.mapper;

import com.pojo.po.AlertRecordPo;
import com.pojo.po.TaskPo;

import java.util.List;

/**
 * @Author SDJin
 * @CreationDate 2023/2/6 16:23
 * @Description ：
 */
public interface AlertRecordMapper {
    /**
     *将AlertRecordPo对象持久化
     * @param alertRecordPo
     * @return
     */
    boolean persistence(AlertRecordPo alertRecordPo);

    /**
     * 查询时间范围内的特定位置的任务记录
     * @param startTime
     * @param endTime
     * @param from
     * @param to
     * @return
     */
    List<AlertRecordPo> selectFromToByTime(long startTime,long endTime,int from,int to);
}
