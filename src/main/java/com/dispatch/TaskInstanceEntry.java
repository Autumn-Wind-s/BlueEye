package com.dispatch;

import com.pojo.instance.TaskInstance;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;



/**
 * @Author SDJin
 * @CreationDate 2023/1/30 22:10
 * @Description ：
 */
@Data
@Slf4j
public class TaskInstanceEntry implements Comparable<TaskInstanceEntry> {


    volatile TaskInstanceList timedTaskList;
    TaskInstanceEntry next;
    TaskInstanceEntry prev;
    private TaskInstance taskInstance;
    private long expireMs;

    public TaskInstanceEntry(TaskInstance taskInstance, long expireMs) {
        this.taskInstance = taskInstance;
        this.expireMs = expireMs;
        this.next = null;
        this.prev = null;
    }

    void remove() {
        TaskInstanceList currentList = timedTaskList;
        while (currentList != null) {
            currentList.remove(this);
            currentList = timedTaskList;
        }
    }

    @Override
    public int compareTo(TaskInstanceEntry o) {
        return ((int) (this.expireMs - o.expireMs));
    }


}
