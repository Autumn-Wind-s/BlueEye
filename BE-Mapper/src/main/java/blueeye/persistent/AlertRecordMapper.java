package blueeye.persistent;


import blueeye.pojo.po.AlertRecordPo;

import java.util.List;

/**
 * @Author SDJin
 * @CreationDate 2023/2/6 16:23
 * @Description ：
 */
public abstract class AlertRecordMapper {



    /**
     * 将AlertRecordPo对象持久化
     *
     * @param alertRecordPo
     * @return
     */
   public abstract boolean persistence(AlertRecordPo alertRecordPo);

    /**
     * 将AlertRecordPo对象持久化
     * @param alertRecordPo
     * @return
     */
   public abstract boolean persistence(List<AlertRecordPo> alertRecordPo);

    /**
     * 查询时间范围内的特定位置的任务记录
     *
     * @param startTime
     * @param endTime
     * @param from
     * @param to
     * @return
     */
   public abstract List<AlertRecordPo> selectFromToByTime(long startTime, long endTime, int from, int to);
}
