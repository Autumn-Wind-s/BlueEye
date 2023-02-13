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
    READY(),
    /**
     * 运行中
     */
    RUNNING(),
    /**
     * 阻塞
     */
    BLOCKING(),
    /**
     * 终止
     */
    TERMINATED(),
    /**
     * 完成
     */
    FINISHED(),
    /**
     * 死亡
     */
    DIE;
    /**
     * 作为重试计数器
     */
    private int num =0;

    InstanceState(int num) {
        this.num = num;
    }

    InstanceState() {
    }


    public int getNum() {
        return num;
    }

    public void add(){
        num++;
    }

    

}
