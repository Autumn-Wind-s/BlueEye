package blueeye.mapping;

import blueeye.pojo.metric.MetricsType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 22:56
 * @Description ：存储了每种类型指标中每个指标实例与进行监控该实例任务的映射关系，便于根据指标实例的类型和名字查询对应的监控任务id
 */
public class MetricTaskIdMapping {
   public ConcurrentHashMap<MetricsType, ConcurrentHashMap<String, Integer>> mapping;

    /**
     * 初始化遍历MetricType的所有枚举值,为每一种指标创建映射集合
     */
    public MetricTaskIdMapping() {
        mapping = new ConcurrentHashMap<>();
        for (MetricsType value : MetricsType.values()) {
            mapping.put(value, new ConcurrentHashMap<>());
        }
    }

    /**
     * 查询指定类型指定名字的指标的任务id
     * @param type
     * @param name
     * @return
     */
    public Integer getId(MetricsType type, String name) {
        return mapping.get(type).get(name);
    }

    /**
     * 判断是否已存在指定类型指定名字的指标（判重）
     * @param type
     * @param name
     * @return
     */
    public Boolean isContains(MetricsType type,String name){
        return mapping.get(type).containsKey(name);
    }

    /**
     * 添加映射
     * @param type
     * @param name
     * @param id
     * @return
     */
    public boolean putMapping(MetricsType type, String name, Integer id) {
        if (!mapping.get(type).containsKey(name)) {
            mapping.get(type).put(name, id);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询指定类型指标的所有指标个体的名字
     * @param type
     * @return
     */
    public List<String> getNames(MetricsType type){
        return new ArrayList<>(mapping.get(type).keySet()) ;
    }
}
