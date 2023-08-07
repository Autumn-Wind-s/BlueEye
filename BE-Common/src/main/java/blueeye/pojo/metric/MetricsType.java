package blueeye.pojo.metric;

/**
 * @Author SDJin
 * @CreationDate 2022/11/3 13:53
 * @Description ：
 */
public enum MetricsType {
    //接口
    Interface,
    //sql语句
    Sql,
    //线程池
    Executors,
    //数据源
    DataSource,
    //打点指标
    Tag,
    //系统指标
    System

}
