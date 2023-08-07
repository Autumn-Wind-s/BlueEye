package blueeye.container;

import blueeye.config.AlertConfig;
import blueeye.config.BlueEyeConfig;
import blueeye.pojo.alert.AlertRule;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 17:27
 * @Description ：
 */
@Data
public class ConfigContainer {
    /**
     * 系统配置文件
     */
    private BlueEyeConfig blueEyeconfig;
    /**
     * 报警配置文件
     */
    private AlertConfig alertConfig;
    /**
     * 报警记录持久化配置，以所有报警记录为粒度，规则为时间(默认30分钟)
     */
    private AtomicLong alertRecordPersistence=new AtomicLong(30*60*1000);
    /**
     * 任务持久化配置，以所有任务为粒度，规则为(默认1天)时间和活跃度
     */
    private AtomicLong taskPersistence=new AtomicLong(24*60*60*1000);
    /**
     * 实例持久化配置，以所有任务实例为粒度，规则为时间(默认5分钟)+状态（完成或死亡）
     */
    private AtomicLong instancePersistence =new AtomicLong(5*60*1000);
    /**
     * 指标数据持久化配置，以单个指标为粒度，规则为时间范围
     */
    private ConcurrentHashMap<Integer, Long> metricPersistence=new ConcurrentHashMap<>();
    /**
     * 指标数据的报警规则，以单个指标为粒度
     */
    private ConcurrentHashMap<Integer, ConcurrentHashMap<Field, AlertRule>> metricAlertConfig=new ConcurrentHashMap<>();

    public ConfigContainer() {
    }
}
