package blueeye.persistent;


import blueeye.pojo.po.TaskInstancePo;

import java.util.List;

/**
 * @Author SDJin
 * @CreationDate 2023/2/6 15:24
 * @Description ：
 */
public abstract class InstanceMapper {



    /**
     *将taskInstancePo对象持久化
     * @param taskInstancePo
     * @return
     */
   public abstract boolean persistence(TaskInstancePo taskInstancePo);

    /**
     * 将taskInstancePo对象持久化
     * @param taskInstancePo
     * @return
     */
   public abstract boolean persistence(List<TaskInstancePo> taskInstancePo);

    /**
     *特定位置实例查询功能
     * @param start
     * @param to
     * @return
     */
   public abstract List<TaskInstancePo> selectFromTo(int start, int to);

    /**
     * 特定位置指定任务实例查询功能：
     * @param taskId
     * @param from
     * @param to
     * @return
     */
  public abstract List<TaskInstancePo> selectFromToByTaskID(int taskId,int from,int to);

}
