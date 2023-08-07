package blueeye.container;


import blueeye.pojo.task.Task;
import blueeye.pojo.task.impl.TimerTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 16:02
 * @Description ：
 */
public class TaskContainer<T extends Task> {

    /**
     * 任务id与任务名称
     */
    private ConcurrentHashMap<Integer, String> mapping;
    /**
     * 任务名称与任务
     */
    private ConcurrentSkipListMap<String, T> map;

    public TaskContainer() {
        mapping = new ConcurrentHashMap<>();
        map = new ConcurrentSkipListMap<>();
    }

    public boolean add(T task) {
        mapping.put(task.getTaskId(), task.getTaskName());
        map.put(task.getTaskName(), task);
        return true;
    }

    public int getSize() {
        return map.size();
    }

    public T remove(int taskId) {
        String remove = mapping.remove(taskId);
        return map.remove(remove);
    }

    public List<T> remove(List<Integer> taskIds) {
        List<T> ts = new ArrayList<>();
        for (Integer taskId : taskIds) {
            ts.add(remove(taskId));
        }
        return ts;
    }

    public List<Integer> getTaskIds() {
        return mapping.keySet().stream().collect(Collectors.toList());

    }

    public List<T> selectPageByName(String name, int page, int pageSize) {
        //使用流处理，简单高效
        return map.values().stream().filter(t -> {
            return t.getTaskName().indexOf(name) != -1;
        }).skip((page - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
    }

    public List<T> selectPage(int page, int pageSize) {
        return map.values().stream().skip((page - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
    }

    public T selectById(int id) {
        if (mapping.get(id) != null) {
            return map.get(mapping.get(id));
        } else {
            return null;
        }

    }

    public T selectByName(String name) {
        return map.get(name);
    }
}
