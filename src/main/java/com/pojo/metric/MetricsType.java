package com.pojo.metric;

/**
 * @Author SDJin
 * @CreationDate 2022/11/3 13:53
 * @Description ：
 */
public enum MetricsType {
    //接口
    Request,
    //sql语句
    Sql,
    //线程池
    Executors,
    //系统指标
    System,
    //打点指标
    Tag
}
