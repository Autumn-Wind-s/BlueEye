package blueeye.container;

import blueeye.pojo.metric.MetricData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 17:17
 * @Description ：
 */
public class MetricDataContainer {
    private ConcurrentHashMap<Integer, ConcurrentLinkedDeque<MetricData>> map;


    public MetricDataContainer() {
        map=new ConcurrentHashMap<>();
    }

    /**
     * 添加数据
     * @param data
     */
    public void addMetricData(MetricData data){
        ConcurrentLinkedDeque<MetricData> orDefault = map.getOrDefault(data.getTaskId(), new ConcurrentLinkedDeque<>());
        orDefault.addLast(data);
        //防止第一次添加
        map.put(data.getTaskId(),orDefault);
    }

    /**
     * 获取最新数据
     * @param taskId
     * @return
     */
    public MetricData getLastMetricData(int taskId){
        return map.containsKey(taskId)?map.get(taskId).getLast():null;
    }

    /**
     * 查找时间范围内的数据
     * @param taskId
     * @param startTime
     * @param endTime
     * @return
     */
    public List<MetricData> getMetricDataByTime(int taskId,long startTime,long endTime){
        List<MetricData> res=new ArrayList<>();
        ConcurrentLinkedDeque<MetricData> metricData = map.get(taskId);
        //有提前结束遍历条件，相比于流效率更高
        if (metricData.isEmpty()||metricData.getFirst().getCreateTime().getTime()>endTime||metricData.getLast().getCreateTime().getTime()<startTime){
            return res;
        }
        for (MetricData data : metricData) {
            if(data.getCreateTime().getTime()>endTime){
                break;
            }
            if(data.getCreateTime().getTime()>startTime&&data.getCreateTime().getTime()<endTime){
                res.add(data);
            }
        }
        return res;
    }

    /**
     * 删除指定时间点之前的数据并返回
     * @param taskId
     * @param time
     * @return
     */
    public List<MetricData> deleteMetricDataByTime(int taskId,long time){
        ConcurrentLinkedDeque<MetricData> metricData = map.get(taskId);
        List<MetricData> res = new ArrayList<>();
        while(!metricData.isEmpty()&&metricData.peekFirst().getCreateTime().getTime()<=time){
            res.add(metricData.pollFirst());
        }
        return res;
    }
}
