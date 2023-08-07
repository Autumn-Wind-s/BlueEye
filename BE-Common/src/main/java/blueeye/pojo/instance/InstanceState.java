package blueeye.pojo.instance;

/**
 * @Author SDJin
 * @CreationDate 2023/2/5 19:58
 * @Description ：
 */
public enum InstanceState {
    /**
     * 就绪
     */
    READY,
    /**
     * 运行中
     */
    RUNNING,
    /**
     * 阻塞
     */
    BLOCKING,
    /**
     * 终止                //处于阻塞状态的任务实例无法被人为终止
     */
    TERMINATED,
    /**
     * 完成
     */
    FINISHED,
    /**
     * 死亡
     */
    DIE;


}