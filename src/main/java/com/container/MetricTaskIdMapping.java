package com.container;

import com.pojo.metric.MetricsType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 22:56
 * @Description ：
 */
public class MetricTaskIdMapping {
   private ConcurrentHashMap<MetricsType, ConcurrentHashMap<String, Integer>> mapping;

    /**
     * 初始化遍历MetricType的所有枚举值,为每一种指标创建映射集合
     */
    public MetricTaskIdMapping() {
        mapping = new ConcurrentHashMap<>();
        for (MetricsType value : MetricsType.values()) {
            mapping.put(value, new ConcurrentHashMap<>());
        }
    }

    public Integer getId(MetricsType type, String name) {
        return mapping.get(type).get(name);
    }

    public boolean putMapping(MetricsType type, String name, Integer id) {
        if (!mapping.get(type).containsKey(name)) {
            mapping.get(type).put(name, id);
            return true;
        } else {
            return false;
        }
    }

    public List<String> getNames(MetricsType type){
        return new ArrayList<>(mapping.get(type).keySet()) ;
    }
}
