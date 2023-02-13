package com.pojo.task;


import lombok.Data;

/**
 * @Author SDJin
 * @CreationDate 2023/2/7 19:36
 * @Description ：监控任务的区别体现在监控逻辑，也就是execute方法，利用回调机制避免创建过多类，且保证的可扩展性
 */
@Data
public class MonitorTask extends TimerTask  {
    private final ExecuteCallback monitorCallback;


    @Override
   public String execute() throws Exception{
        return monitorCallback.execute();
    }
}
