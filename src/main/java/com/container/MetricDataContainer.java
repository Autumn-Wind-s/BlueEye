package com.container;

import com.pojo.metric.MetricData;

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

    public void addMetricData(MetricData data){
        ConcurrentLinkedDeque<MetricData> orDefault = map.getOrDefault(data.getTaskId(), new ConcurrentLinkedDeque<>());
        orDefault.addLast(data);
    }

    public MetricData getLastMetricData(int taskId){

        return map.containsKey(taskId)?map.get(taskId).getLast():null;
    }

    public List<MetricData> getMetricDataByTime(int taskId,long startTime,long endTime){
        List<MetricData> res=new ArrayList<>();
        ConcurrentLinkedDeque<MetricData> metricData = map.get(taskId);
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
}
