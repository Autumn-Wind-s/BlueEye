package blueeye.persistent;


import blueeye.pojo.po.TaskPo;

import java.util.List;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 16:29
 * @Description ：
 */
public abstract class TaskMapper {


    /**
     * 将TaskPo对象持久化
     *
     * @param taskPo
     * @return
     */
    public abstract boolean persistence(TaskPo taskPo);

    /**
     * 将TaskPo对象持久化
     * @param taskPo
     * @return
     */
    public abstract boolean persistence(List<TaskPo> taskPo);

    /**
     * 任务名称模糊分页查询
     *
     * @param name
     * @param type
     * @param page
     * @param pageSize
     * @return
     */
    public abstract List<Integer> selectPageByName(String name, int type, int page, int pageSize);

    /**
     * 分页查询
     *
     * @param type
     * @param page
     * @param pageSize
     * @return
     */
    public abstract List<Integer> selectPage(int type, int page, int pageSize);

    /**
     * 多任务id查询
     *
     * @param ids
     * @return
     */
    public abstract List<TaskPo> selectByIds(int[] ids);

    /**
     * 单任务id查询
     *
     * @param id
     * @return
     */
    public abstract TaskPo selectById(int id);

    /**
     * 任务名称精确查询
     *
     * @param name
     * @return
     */
    public abstract TaskPo selectByName(String name);

    /**
     * 任务修改
     *
     * @param taskPo
     * @return
     */
    public abstract boolean update(TaskPo taskPo);
}
