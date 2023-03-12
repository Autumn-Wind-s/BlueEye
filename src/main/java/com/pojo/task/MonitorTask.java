package com.pojo.task;


import com.pojo.po.TaskPo;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author SDJin
 * @CreationDate 2023/2/7 19:36
 * @Description ：监控任务的区别体现在监控逻辑，也就是execute方法，利用回调机制避免创建过多类，且保证的可扩展性
 */
@Data
public class MonitorTask extends TimerTask  {
    private final ExecuteCallback monitorCallback;

    /**
     * 通过构造器实现反压缩
     * @param taskPo
     */
    public MonitorTask(TaskPo taskPo){
        monitorCallback=null;
        //todo 压缩逻辑，由于monitorCallback是回调赋值，相当于动态的创建，如何保证压缩与反压缩前后monitorCallback的执行逻辑一致是个难点
        //todo 初步考虑将ExecuteCallback序列化和反序列化


    }
    public MonitorTask(ExecuteCallback callback){
        monitorCallback=callback;
    }
    @Override
   public String execute() throws Exception{
        return monitorCallback.execute();
    }

    @Override
    public TaskPo compress() {
        //todo 压缩逻辑
        return null;
    }
}
