package com.mapper.impl;

import com.mapper.TaskMapper;
import com.pojo.po.TaskPo;

import java.util.List;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 17:01
 * @Description ：
 */
public class JdbcTaskMapper implements TaskMapper {
    @Override
    public boolean persistence(TaskPo taskPo) {
        return false;
    }

    @Override
    public List<Integer> selectPageByName(String name, int type, int page, int pageSize) {
        return null;
    }

    @Override
    public List<Integer> selectPage(int type, int page, int pageSize) {
        return null;
    }

    @Override
    public List<TaskPo> selectByIds(int[] ids) {
        return null;
    }

    @Override
    public TaskPo selectById(int id) {
        return null;
    }

    @Override
    public TaskPo selectByName(String name) {
        return null;
    }

    @Override
    public boolean update(TaskPo taskPo) {
        return false;
    }
}
