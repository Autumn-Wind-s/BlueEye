package com.pojo.task;

import lombok.Data;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * @Author SDJin
 * @CreationDate 2023/2/7 19:22
 * @Description ：不同脚本任务的区别在于执行的脚本，该区别可以用属性体现,execute中逻辑是不变的，可以用一个类确定
 */
@Data
public class ScriptTask extends TimerTask {
    /**
     * 执行的shell命令
     */
    private final String shell;

    @Override
    public String execute() throws Exception {
        //用java.lang.Runtime类的exec()方法执行shell脚本
        return null;
    }
}
