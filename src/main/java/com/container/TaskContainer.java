package com.container;

import com.pojo.task.Task;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 16:02
 * @Description ：
 */
public class TaskContainer<T extends Task> {
    private ConcurrentHashMap<Integer, String> mapping;
    private ConcurrentSkipListMap<String, T> map;

    public boolean add(T task) {

        return true;
    }

    public List<Task> selectPageByName(String name, int page, int pageSize) {
        return null;
    }

    public List<Task> selectPage(int page, int pageSize) {
        return null;
    }

    public Task selectById(int id) {
        return null;
    }

    public Task selectByName(String name) {
        return null;
    }
}
