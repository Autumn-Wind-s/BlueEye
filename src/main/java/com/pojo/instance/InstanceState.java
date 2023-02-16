package com.pojo.instance;

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
     * 终止
     */
    TERMINATED0,
    /**
     * 完成
     */
    FINISHED,
    /**
     * 死亡
     */
    DIE;

    static {
        //todo  读取配置中心的重试阈值配置，没有则取默认3
        // 根据配置使用EnumUtil.addEnum动态增加枚举值
        //  然后正常使用枚举即可
    }
}