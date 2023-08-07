package blueeye.container;


import blueeye.pojo.alert.AlertRecord;
import blueeye.pojo.metric.MetricData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 17:28
 * @Description ：
 */
public class AlertRecordContainer {
    ConcurrentLinkedDeque<AlertRecord> records;

    public AlertRecordContainer() {
        records=new ConcurrentLinkedDeque<>();
    }

    /**
     * 查询指定时间范围内的报警记录
     * @param startTime
     * @param endTime
     * @return
     */
    public List<AlertRecord> getAlertRecordByTime( long startTime, long endTime){
        //有提前结束遍历条件，相比于流效率更高
        List<AlertRecord> alertRecords = new ArrayList<>();
        if (records.isEmpty()||records.getFirst().getAlertTime().getTime()>endTime||records.getLast().getAlertTime().getTime()<startTime){
            return alertRecords;
        }
        for (AlertRecord record : alertRecords) {
            if(record.getAlertTime().getTime()>endTime){
                break;
            }
            if(record.getAlertTime().getTime()>startTime&&record.getAlertTime().getTime()<endTime){
                alertRecords.add(record);
            }
        }
        return alertRecords;
    }

    /**
     * 添加报警记录
     */
    public void addAlertRecord(AlertRecord record){
        records.add(record);
    }
    /**
     * 查询返回指定时间点之前的报警记录，并从容器中删除
     * @param time
     * @return
     */
    public List<AlertRecord> deleteAlertRecordByTime(long time){
        List<AlertRecord> res = new ArrayList<>();
        while (!records.isEmpty()&&records.peekFirst().getAlertTime().getTime()<=time){
            res.add(records.pollFirst());
        }
        return res;
    }
}
