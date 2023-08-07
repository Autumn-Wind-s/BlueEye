package blueeye.pojo.metric.dataSourece;

import blueeye.pojo.metric.MetricData;
import lombok.Data;
import lombok.NonNull;

/**
 * @Author SDJin
 * @CreationDate 2023/2/25 15:01
 * @Description ：
 */
@Data
public class DataSourceData extends MetricData {
    /**
     * 数据库名称
     */
    private String databaseName;
    /**
     * 数据库类型
     */
    private String databaseType;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 访问地址
     */
    private String url;
    /**
     * 加载类
     */
    private String driverClass;
    /**
     * 初始化连接数
     */
    private Integer initialPoolSize;
    /**
     * 最大连接数
     */
    private Integer maxPoolSize;
    /**
     * 最小连接数
     */
    private Integer minPoolSize;
    /**
     * 是否只读
     */
    private Boolean readOnly = false;
    /**
     * 默认隔离级别
     */
    private Integer defaultTransactionIsolation;
    /**
     * 是否自动提交
     */
    private Boolean autoCommit = false;
    /**
     * 等待连接线程数
     */
    private Integer threadsAwaiting;
    /**
     * 空闲连接数
     */
    private Integer ileConnections;
    /**
     * 活跃连接数
     */
    private Integer busyConnections;
    /**
     * 当前总连接数
     */
    private Integer Connections;

}
