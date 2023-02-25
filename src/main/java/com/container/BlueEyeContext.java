package com.container;

import com.dispatch.TimerScheduler;
import com.mapper.MapperManager;
import com.pojo.task.ExecuteCallback;
import com.pojo.task.MonitorTask;
import com.rdb.Rdb;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author SDJin
 * @CreationDate 2023/2/8 13:47
 * @Description ：
 */
public class BlueEyeContext {
    public static DataCenter dataCenter;
    public static TimerScheduler timerScheduler;
    public static Rdb rdb;
    public static MapperManager mapperManager;

    public void init(ServletContext context) {
        //todo
    }

    public  void destroy(){
        //todo
    }
    /**
     * 作为提供给用户进行线程池监控的入口
     *
     * @param executorName
     * @param executors
     */
    public static void monitorExecutor(String executorName, ExecutorService executors, long cycle, int order) {
        // 生成监控任务，定义监控逻辑
        MonitorTask monitorTask = new MonitorTask(new ExecuteCallback() {
            @Override
            public String execute() throws Exception {
                return executorName;
            }
        });
        //分配任务id
        //存储任务
        //存储线程池名称和任务id的映射
        //生成任务实例传入任务调度器执行

    }

    /**
     * 作为提供给用户进行数据源监控的入口
     * @param dataSourceName
     * @param dataSource
     */
    public static void monitorDataSource(String dataSourceName, DataSource dataSource){
        // 生成监控任务，定义监控逻辑
        //分配任务id
        //存储任务
        //存储数据源名称和任务id的映射
        //生成任务实例传入任务调度器执行
    }
    /**
     * 作为用户自定义调度任务添加的入口
     *
     * @param taskName
     * @param taskDescription
     * @param preTask
     * @param executeCallback
     */
    public static void AddCustomizeTask(String taskName, String taskDescription, List<Integer> preTask, ExecuteCallback executeCallback) {

    }

    /**
     * 作为用户自定义调度任务添加的入口，添加的同时会生成实例去执行
     *
     * @param taskName
     * @param taskDescription
     * @param preTask
     * @param executeCallback
     * @param cycle
     * @param order
     */
    public static void AddCustomizeTask(String taskName, String taskDescription, List<Integer> preTask, ExecuteCallback executeCallback, long cycle, int order) {

    }

}
