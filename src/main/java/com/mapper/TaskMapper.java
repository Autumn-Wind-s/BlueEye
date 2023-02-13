package com.mapper;

import com.pojo.po.TaskPo;

import java.util.List;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 16:29
 * @Description ：
 */
public interface TaskMapper {
    /**
     *将TaskPo对象持久化
     * @param taskPo
     * @return
     */
    boolean persistence(TaskPo taskPo);

    /**
     * 任务名称模糊分页查询
     * @param name
     * @param type
     * @param page
     * @param pageSize
     * @return
     */
    List<Integer> selectPageByName(String name, int type, int page, int pageSize);

    /**
     * 分页查询
     * @param type
     * @param page
     * @param pageSize
     * @return
     */
    List<Integer> selectPage(int type,int page,int pageSize);

    /**
     * 多任务id查询
     * @param ids
     * @return
     */
    List<TaskPo> selectByIds(int[] ids);

    /**
     * 单任务id查询
     * @param id
     * @return
     */
    TaskPo selectById(int id);

    /**
     * 任务名称精确查询
     * @param name
     * @return
     */
    TaskPo selectByName(String name);

    /**
     * 任务修改
     * @param taskPo
     * @return
     */
    boolean update(TaskPo taskPo);
}
