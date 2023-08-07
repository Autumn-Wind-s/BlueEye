package blueeye.center;


import blueeye.container.*;
import blueeye.mapping.MetricTaskIdMapping;
import blueeye.pojo.metric.MetricData;
import blueeye.pojo.metric.MetricsType;
import blueeye.pojo.metric.Tag.TagData;
import blueeye.pojo.metric.dataSourece.DataSourceData;
import blueeye.pojo.metric.executor.ExecutorData;
import blueeye.pojo.metric.intf.InterfaceData;
import blueeye.pojo.metric.sql.SqlData;
import blueeye.pojo.task.impl.TimerTask;
import blueeye.pojo.task.impl.intf.InterfaceTask;
import blueeye.pojo.task.impl.monitor.MonitorTask;
import blueeye.pojo.task.impl.script.ScriptTask;
import blueeye.rdb.Snapshot;
import lombok.Data;

import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 17:27
 * @Description ：
 */
@Data
public class DataCenter {

    /**
     * 元任务容器
     */
    private TaskContainer<MonitorTask> metaTasks;
    /**
     * 监控(调度）任务容器
     */
    private TaskContainer<MonitorTask> monitorTasks;
    /**
     * 接口任务容器
     */
    private TaskContainer<InterfaceTask> interfaceTasks;
    /**
     * 脚本任务容器
     */
    private TaskContainer<ScriptTask> scriptTasks;
    /**
     * 指标数据容器
     */
    private MetricDataContainer metrics;
    /**
     * 实例容器
     */
    private InstanceContainer instances;
    /**
     * 报警记录容器
     */
    private AlertRecordContainer records;
    /**
     * 监控指标与对应监控任务id映射
     */
    private MetricTaskIdMapping mapping;
    /**
     * 任务原子id
     */
    private AtomicInteger taskId;
    /**
     * 实例原子id
     */
    private AtomicInteger instanceId;
    /**
     * 数据原子id
     */
    private AtomicInteger dataId;
    /**
     * 报警记录原子id
     */
    private AtomicInteger recordId;
    /**
     * 配置容器
     */
    private ConfigContainer config;

    public DataCenter(DataCenterBuilder builder) {
        metaTasks = builder.metaTasks;
        metrics = builder.metrics;
        monitorTasks = builder.monitorTasks;
        interfaceTasks = builder.interfaceTasks;
        scriptTasks = builder.scriptTasks;
        instanceId = builder.instanceId;
        recordId = builder.recordId;
        mapping = builder.mapping;
        taskId = builder.taskId;
        instances = builder.instances;
        dataId = builder.dataId;
        records = builder.records;
        config = builder.config;
    }

    public DataCenter() {
    }

    /**
     * 作为用户自定义数据的打点上传入口
     *
     * @param name tag名称
     * @param data 数据
     */
    public <T> void uploadTagData(String name, T data) {
        //先判断是否为该类tag的首次上传
        Integer id = mapping.getId(MetricsType.Tag, name);
        if (id == null) {
            //首次上传，为该tag绑定虚拟任务id,存储映射
            id = taskId.incrementAndGet();
            mapping.putMapping(MetricsType.Tag, name, id);
        }

        //获取对应类型的Tag对象
        TagData<T> tagData = new TagData<T>(data);
        tagData.setId(dataId.getAndIncrement());
        tagData.setTaskId(id);
        tagData.setInstanceId(instanceId.getAndIncrement());
        tagData.setCreateTime(new Timestamp(System.currentTimeMillis()));
        //添加对象
        this.metrics.addMetricData(tagData);
    }

    /**
     * 根据id查找TimerTask
     *
     * @param id
     * @return
     */
    public TimerTask getTimerTaskById(Integer id) {
        TimerTask task = metaTasks.selectById(id);
        if (task == null) {
            task = monitorTasks.selectById(id);
        }
        if (task == null) {
            task = interfaceTasks.selectById(id);
        }
        if (task == null) {
            task = scriptTasks.selectById(id);
        }
        return task;
    }

    /**
     * 作为数据库连接池数据上传入口
     *
     * @param data
     */
    public void uploadDataSource(DataSourceData data) {
        metrics.addMetricData(data);
    }

    /**
     * 数据无关，直接上传最新数据对象
     *
     * @param data
     */
    public void uploadExecutorData(ExecutorData data) {
        metrics.addMetricData(data);
    }

    /**
     * 最新数据与之前数据无关，直接上传数据对象
     *
     * @param data
     */
    public void uploadSystemData(MetricData data) {
        metrics.addMetricData(data);
    }

    /**
     * 接口qps统计计数，依赖之前数据
     *
     * @param interfaceName
     */
    public void uploadInterfaceData(String interfaceName, long computingTime, boolean isSuccess) {
        //根据interfaceName查询taskId
        Integer id = mapping.getId(MetricsType.Interface, interfaceName);
        //根据id获取之前的最新数据
        InterfaceData lastMetricData = (InterfaceData) metrics.getLastMetricData(id);
        //生成最新数据
        InterfaceData data = new InterfaceData();
        data.setId(dataId.getAndIncrement());
        data.setTaskId(id);
        data.setCreateTime(new Timestamp(System.currentTimeMillis()));
        data.setInstanceId(instanceId.getAndIncrement());
        data.setTotalCount(lastMetricData.getTotalCount() + 1);
        data.setSuccessCount(isSuccess ? lastMetricData.getSuccessCount() + 1 : lastMetricData.getSuccessCount());
        data.setFailCount(isSuccess ? lastMetricData.getFailCount() : lastMetricData.getFailCount() + 1);
        data.setMinComputingTime(Math.min(lastMetricData.getMinComputingTime(), computingTime));
        data.setMaxComputingTime(Math.max(lastMetricData.getMaxComputingTime(), computingTime));
        data.setAverageComputingTime((lastMetricData.getAverageComputingTime() * lastMetricData.getTotalCount() + computingTime) / data.getTotalCount());
        metrics.addMetricData(data);
    }


    /**
     * sql执行次数统计和平均耗时依赖之前数据
     *
     * @param sql
     * @param time
     */
    public void uploadSqlData(String sql, long time) {
        //先查出taskId
        Integer id = mapping.getId(MetricsType.Sql, sql);
        //查出历史最新数据
        SqlData lastMetricData = (SqlData) metrics.getLastMetricData(id);
        //生成最新数据
        SqlData sqlData = new SqlData();
        sqlData.setCreateTime(new Timestamp(System.currentTimeMillis()));
        sqlData.setId(dataId.getAndIncrement());
        sqlData.setTaskId(id);
        sqlData.setInstanceId(instanceId.getAndIncrement());
        sqlData.setSql(sql);
        sqlData.setExecuteCount(lastMetricData.getExecuteCount() + 1);
        sqlData.setRecentComputingTime(time);
        sqlData.setAverageComputingTime((lastMetricData.getAverageComputingTime() * lastMetricData.getExecuteCount() + time) / sqlData.getExecuteCount());
        metrics.addMetricData(sqlData);
    }


    /**
     * 创建快照对象
     *
     * @return
     */
    public Snapshot createSnapshot() {
        return new DataSnapshot(this);
    }

    /**
     * 数据恢复
     *
     * @param snapshot
     */
    public void restoreSnapshot(Snapshot snapshot) {
        DataSnapshot dataSnapshot = (DataSnapshot) snapshot;
        metaTasks = dataSnapshot.metaTasks;
        metrics = dataSnapshot.metrics;
        monitorTasks = dataSnapshot.monitorTasks;
        interfaceTasks = dataSnapshot.interfaceTasks;
        scriptTasks = dataSnapshot.scriptTasks;
        instanceId = dataSnapshot.instanceId;
        recordId = dataSnapshot.recordId;
        mapping = dataSnapshot.mapping;
        taskId = dataSnapshot.taskId;
        instances = dataSnapshot.instances;
        dataId = dataSnapshot.dataId;
        records = dataSnapshot.records;
        config = dataSnapshot.config;
    }

    /**
     * 建造器，作用于项目初始化时构造数据中心
     */
    public static final class DataCenterBuilder {
        private TaskContainer metaTasks;
        private TaskContainer monitorTasks;
        private TaskContainer interfaceTasks;
        private TaskContainer scriptTasks;
        private MetricDataContainer metrics;
        private InstanceContainer instances;
        private AlertRecordContainer records;
        private MetricTaskIdMapping mapping;
        private AtomicInteger taskId;
        private AtomicInteger instanceId;
        private AtomicInteger dataId;
        private AtomicInteger recordId;
        private ConfigContainer config;

        public DataCenterBuilder() {
        }

        public DataCenterBuilder metaTasks(TaskContainer metaTasks) {
            this.metaTasks = metaTasks;
            return this;
        }
        public DataCenterBuilder monitorTasks(TaskContainer monitorTasks) {
            this.monitorTasks = monitorTasks;
            return this;
        }
        public DataCenterBuilder interfaceTasks(TaskContainer interfaceTasks) {
            this.interfaceTasks = interfaceTasks;
            return this;
        }
        public DataCenterBuilder scriptTasks(TaskContainer scriptTasks) {
            this.scriptTasks = scriptTasks;
            return this;
        }
        public DataCenterBuilder metrics(MetricDataContainer metrics) {
            this.metrics = metrics;
            return this;
        }
        public DataCenterBuilder instances(InstanceContainer instances) {
            this.instances = instances;
            return this;
        }
        public DataCenterBuilder records(AlertRecordContainer records) {
            this.records = records;
            return this;
        }
        public DataCenterBuilder mapping(MetricTaskIdMapping mapping) {
            this.mapping = mapping;
            return this;
        }
        public DataCenterBuilder taskId(AtomicInteger taskId) {
            this.taskId = taskId;
            return this;
        }
        public DataCenterBuilder instanceId(AtomicInteger instanceId) {
            this.instanceId = instanceId;
            return this;
        }
        public DataCenterBuilder dataId(AtomicInteger dataId) {
            this.dataId = dataId;
            return this;
        }
        public DataCenterBuilder recordId(AtomicInteger recordId) {
            this.recordId = recordId;
            return this;
        }
        public DataCenterBuilder config(ConfigContainer config) {
            this.config = config;
            return this;
        }

        public DataCenter build() {
            return new DataCenter(this);
        }
    }

    /**
     * 快照,定义为内部类是为了只提供宽接口给数据中心，作用于项目重启时构造数据中心
     */
    private  class DataSnapshot implements Snapshot {
        private final TaskContainer<MonitorTask> metaTasks;
        private final TaskContainer<MonitorTask> monitorTasks;
        private final TaskContainer<InterfaceTask> interfaceTasks;
        private final TaskContainer<ScriptTask> scriptTasks;
        private final MetricDataContainer metrics;
        private final InstanceContainer instances;
        private final AlertRecordContainer records;
        private final MetricTaskIdMapping mapping;
        private final AtomicInteger taskId;
        private final AtomicInteger instanceId;
        private final AtomicInteger dataId;
        private final AtomicInteger recordId;
        private final ConfigContainer config;

        public DataSnapshot(DataCenter dataCenter) {
            metaTasks = dataCenter.metaTasks;
            monitorTasks = dataCenter.monitorTasks;
            interfaceTasks = dataCenter.interfaceTasks;
            scriptTasks = dataCenter.scriptTasks;
            metrics = dataCenter.metrics;
            instances = dataCenter.instances;
            records = dataCenter.records;
            mapping = dataCenter.mapping;
            taskId = dataCenter.taskId;
            instanceId = dataCenter.instanceId;
            dataId = dataCenter.dataId;
            recordId = dataCenter.recordId;
            config = dataCenter.config;
        }

        public TaskContainer getMetaTasks() {
            return metaTasks;
        }

        public TaskContainer getMonitorTasks() {
            return monitorTasks;
        }

        public TaskContainer getInterfaceTasks() {
            return interfaceTasks;
        }

        public TaskContainer getScriptTasks() {
            return scriptTasks;
        }

        public MetricDataContainer getMetrics() {
            return metrics;
        }

        public InstanceContainer getInstances() {
            return instances;
        }

        public AlertRecordContainer getRecords() {
            return records;
        }

        public MetricTaskIdMapping getMapping() {
            return mapping;
        }

        public AtomicInteger getTaskId() {
            return taskId;
        }

        public AtomicInteger getInstanceId() {
            return instanceId;
        }

        public AtomicInteger getDataId() {
            return dataId;
        }

        public AtomicInteger getRecordId() {
            return recordId;
        }

        public ConfigContainer getConfig() {
            return config;
        }
    }
}
