package blueeye.dispatch;


import blueeye.pojo.instance.TaskInstance;

/**
 * @Author SDJin
 * @CreationDate 2023/1/30 22:19
 * @Description ：
 */
public interface Timer {

    /**
     * 添加一个新任务
     *
     * @param taskInstance
     */
    void add(TaskInstance taskInstance);

    /**
     * 推动指针
     *
     * @param timeout
     */
    void advanceClock(long timeout);

    /**
     * 等待执行的任务
     *
     * @return
     */
    int size();

    /**
     * 关闭服务,剩下的无法被执行
     */
    void shutdown();

}
