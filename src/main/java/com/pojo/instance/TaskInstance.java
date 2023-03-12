package com.pojo.instance;

import com.pojo.Compression.CompressionInterface;
import com.pojo.po.TaskInstancePo;
import lombok.Data;

import java.sql.Timestamp;
import java.util.concurrent.Callable;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 17:14
 * @Description ：
 */
@Data
public class TaskInstance implements Callable, CompressionInterface<TaskInstancePo> {
    private final Integer instanceId;
    private final Integer taskId;
    private InstanceState state;
    private Timestamp executionTime;
    private Timestamp finishTime;
    private Long cycle;
    private String record;
    private Integer order;

    /**
     * 判断前置任务是否已执行
     *
     * @return
     */
    public boolean checkPre() {
        //根据任务id到前置任务id数组，遍历数组判断每个前置任务是否有已执行完成的实例
        return true;
    }

    /**
     * 通过构造器实现反压缩
     * @param instancePo
     */
    public TaskInstance(TaskInstancePo instancePo){
       //无用代码，暂时避免报错，后期删掉
        this.instanceId=0;
        this.taskId=0;
        //todo 反压缩逻辑

    }
    @Override
    public Object call() throws Exception {
        //根据任务id查询任务对象，执行任务的execute()

        //将返回值append到record中作为实例执行记录
        return null;
    }

    @Override
    public TaskInstancePo compress() {
        //todo 压缩逻辑
        return null;
    }
}
