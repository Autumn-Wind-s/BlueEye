package blueeye.pojo.task.impl.monitor;


/**
 * @Author SDJin
 * @CreationDate 2023/2/7 19:50
 * @Description ：回调接口，作为不同任务的执行逻辑的抽象，也是一个提供给用户的扩展点
 */
public interface ExecuteCallback  {

    /**
     * 任务的真正逻辑,任务执行体,返回值为任务执行记录,注意抓取异常，保证即使发生异常，在finally块中能也将不完整的记录返回
     * @return 任务执行记录
     * @param instanceId
     * @throws Exception 执行异常
     */
    String execute(int taskId,int instanceId) throws Exception;
}
