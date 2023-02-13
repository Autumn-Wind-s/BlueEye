package com.dispatch;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * @Author SDJin
 * @CreationDate 2023/1/30 22:12
 * @Description ：
 */
@Data
@Slf4j
public class TaskInstanceList implements Delayed {


    /**
     * TimerTaskList 环形链表使用一个虚拟根节点root
     */
    private TaskInstanceEntry root = new TaskInstanceEntry(null, -1);
    /**
     * bucket的过期时间
     */
    private AtomicLong expiration = new AtomicLong(-1L);

    {
        root.next = root;
        root.prev = root;
    }

    public long getExpiration() {
        return expiration.get();
    }

    /**
     * 设置bucket的过期时间,设置成功返回true
     *
     * @param expirationMs
     * @return
     */
    boolean setExpiration(long expirationMs) {
        return expiration.getAndSet(expirationMs) != expirationMs;
    }

    public boolean addTask(TaskInstanceEntry entry) {
        boolean done = false;
        while (!done) {
            // 如果TimerTaskEntry已经在别的list中就先移除,同步代码块外面移除,避免死锁,一直到成功为止
            entry.remove();
            synchronized (this) {
                if (entry.timedTaskList == null) {
                    // 加到链表的末尾
                    entry.timedTaskList = this;
                    TaskInstanceEntry tail = root.prev;
                    entry.prev = tail;
                    entry.next = root;
                    tail.next = entry;
                    root.prev = entry;
                    done = true;
                }
            }
        }
        return true;
    }

    /**
     * 从 TimedTaskList 移除指定的 timerTaskEntry
     *
     * @param entry
     */
    public void remove(TaskInstanceEntry entry) {
        synchronized (this) {
            if (entry.getTimedTaskList().equals(this)) {
                entry.next.prev = entry.prev;
                entry.prev.next = entry.next;
                entry.next = null;
                entry.prev = null;
                entry.timedTaskList = null;
            }
        }
    }

    /**
     * 移除所有
     */
    public synchronized void clear(Consumer<TaskInstanceEntry> entry) {
        TaskInstanceEntry head = root.next;
        while (!head.equals(root)) {
            remove(head);
            entry.accept(head);
            head = root.next;
        }
        expiration.set(-1L);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return Math.max(0, unit.convert(expiration.get() - System.currentTimeMillis(), TimeUnit.MILLISECONDS));
    }

    @Override
    public int compareTo(Delayed o) {
        if (o instanceof TaskInstanceList) {
            return Long.compare(expiration.get(), ((TaskInstanceList) o).expiration.get());
        }
        return 0;
    }


}
