package blueeye.container;

import blueeye.pojo.instance.InstanceState;
import blueeye.pojo.instance.TaskInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 17:06
 * @Description ：存储实例，不包括虚拟实例
 */
public class InstanceContainer {
    /**
     * 使用List是为了方便分页查询
     */
    /**
     * 任务id与该任务的实例id
     */
    private ConcurrentSkipListMap<Integer, List<Integer>> mapping;
    /**
     * 实例id与实例
     */
    private ConcurrentSkipListMap<Integer, TaskInstance> map;

    public InstanceContainer() {
        mapping=new ConcurrentSkipListMap<>();
        map=new ConcurrentSkipListMap<>();
    }

    /**
     * 查询指定任务的实例条数
     *
     * @param taskId
     * @return
     */
    public int getNumByTaskId(int taskId) {
        if (mapping.containsKey(taskId)) {
            return mapping.get(taskId).size();
        } else {
            return 0;
        }

    }

    /**
     * 判断是否存在指定任务的实例
     *
     * @param taskId
     * @return
     */
    public boolean contain(int taskId) {
        return mapping.containsKey(taskId);
    }

    /**
     * 获取是否状态为已完成或死亡的任务实例
     *
     * @param
     * @return
     */
    public List<TaskInstance> getInertiaTaskInstance() {
        List<TaskInstance> taskInstances = new ArrayList<>();
        for (TaskInstance value : map.values()) {
            if (value.getState().equals(InstanceState.FINISHED) || value.getState().equals(InstanceState.DIE)) {
                taskInstances.add(value);
            }
        }
        return taskInstances;
    }

    public TaskInstance remove(int instance) {
        TaskInstance remove = map.remove(instance);
        List<Integer> list = mapping.get(remove.getTaskId());
        list.remove(instance);
        if (list.isEmpty()) {
            mapping.remove(remove.getTaskId());
        }
        return remove;
    }

    public List<TaskInstance> remove(List<TaskInstance> instances) {
        List<TaskInstance> taskInstances = new ArrayList<>();
        for (TaskInstance instance : instances) {
            taskInstances.add(remove(instance.getInstanceId()));
        }
        return taskInstances;
    }

    /**
     * 查询所有实例条数
     *
     * @return
     */
    public int getNum() {
        return map.size();
    }

    /**
     * 查询指定任务的指定位置的实例
     *
     * @param taskId
     * @param start
     * @param end
     * @return
     */
    public List<TaskInstance> selectByPositionAndTaskId(int taskId, int start, int end) {
        List<Integer> collect = mapping.get(taskId).stream().skip(start - 1).limit(end - start + 1).collect(Collectors.toList());
        List<TaskInstance> res = new ArrayList<>();
        for (Integer integer : collect) {
            res.add(map.get(integer));
        }
        return res;
    }

    /**
     * 查询指定位置的实例
     *
     * @param start
     * @param end
     * @return
     */
    public List<TaskInstance> selectByPosition(int start, int end) {
        return map.values().stream().skip(start - 1).limit(end).collect(Collectors.toList());
    }

    /**
     * 获取所有running,blocking,ready状态的实例
     * @return
     */
    public List<TaskInstance> getActiveInstance(){
      return   map.values().stream().filter(instance -> {
          if(instance.getState()!=InstanceState.DIE&&instance.getState()!=InstanceState.FINISHED&&instance.getState()!=InstanceState.TERMINATED){
              return true;
          }
          return false;
      }).collect(Collectors.toList());
    }
    /**
     * 根据实例id查询实例
     *
     * @param instanceId
     * @return
     */
    public TaskInstance selectByInstanceId(int instanceId) {
        return map.get(instanceId);
    }

    /**
     * 添加实例
     *
     * @param instance
     */
    public void addInstance(TaskInstance instance) {
        List<Integer> orDefault = mapping.getOrDefault(instance.getTaskId(), new ArrayList<>());
        orDefault.add(instance.getInstanceId());
        mapping.put(instance.getTaskId(), orDefault);
        map.put(instance.getInstanceId(), instance);
    }

}
