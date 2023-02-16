package com.mapper;

/**
 * @Author SDJin
 * @CreationDate 2023/2/16 17:39
 * @Description ：主要是做数据持久化的初始化和终止工作
 */
public abstract class MapperManager {
    AlertRecordMapper alertRecordMapper ;
    InstanceMapper instanceMapper;
    MetricDataMapper metricDataMapper;
    TaskMapper taskMapper;
    /**
     * 主要做数据持久化的初始化工作
     */
    abstract void init();

    /**
     * 主要做数据持久化的终止工作，例如释放资源
     */
    abstract void destroy();
}
