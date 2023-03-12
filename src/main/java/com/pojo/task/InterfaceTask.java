package com.pojo.task;

import com.pojo.po.TaskPo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @Author SDJin
 * @CreationDate 2023/2/7 19:21
 * @Description ：不同的接口任务区别在于调度的接口不同，该区别可以用属性体现，execute中的代码可以确定，可以用一个类确定
 */
public class InterfaceTask extends TimerTask {
    private String url;
    private List<String> params;
    private String requestMode;
    private Map<String, String> requestHeaders;

    /**
     *通过构造器实现反压缩
     * @param taskPo
     */
    public InterfaceTask(TaskPo taskPo){
        //todo 反压缩逻辑
    }

    @Override
    public String execute() throws Exception {
        //使用HttpClient调用，将调用结果作为执行记录返回
        return null;
    }

    @Override
    public TaskPo compress() {
        //todo 压缩逻辑
        return null;
    }
}
