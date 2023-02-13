package com.container;

import com.pojo.task.ExecuteCallback;
import com.rdb.Snapshot;
import com.pojo.metric.ExecutorsData;
import com.pojo.metric.MetricsType;
import com.pojo.metric.SystemData;
import com.pojo.metric.TagData;
import com.pojo.task.InterfaceTask;
import com.pojo.task.MonitorTask;
import com.pojo.task.ScriptTask;
import lombok.Data;

import java.sql.Timestamp;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 17:27
 * @Description ：
 */
@Data
public class DataCenter {


    private TaskContainer<MonitorTask> metaTasks;
    private TaskContainer<MonitorTask> monitorTasks;
    private TaskContainer<InterfaceTask> interfaceTasks;
    private TaskContainer<ScriptTask> scriptTasks;
    private MetricDataContainer metrics;
    private InstanceContainer instances;
    private AlertRecordContainer records;
    private MetricTaskIdMapping mapping;
    private AtomicInteger taskId;
    private AtomicInteger instanceId;
    private AtomicInteger dataId;
    private AtomicInteger recordId;
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
     * @param name  tag名称
     * @param clazz tag所记录的数据的类型
     * @param data  数据
     */
    public void uploadTagData(String name, Class clazz, Object data) {
        //先判断是否为该tag的首次上传
        Integer id = mapping.getId(MetricsType.Tag, name);
        if (id == null) {
            //首次上传，分配给tag任务id,存储映射
            id = taskId.incrementAndGet();
            mapping.putMapping(MetricsType.Tag, name, id);
        }
        //获取对应类型的Tag对象
        TagData tagData = getTagMetric(clazz, data);
        tagData.setId(dataId.incrementAndGet());
        tagData.setTaskId(id);
        tagData.setCreateTime(new Timestamp(System.currentTimeMillis()));
        //添加对象
        this.metrics.addMetricData(tagData);
    }

    public static <T> TagData<T> getTagMetric(Class<T> clz, Object o) {
        if (clz.isInstance(o)) {
            return new TagData<>((T) o);
        }
        return null;
    }

    /**
     * 最新数据与之前数据无关，直接上传数据对象
     *
     * @param data
     */
    public void uploadSystemData(SystemData data) {

    }

    /**
     * 接口qps统计计数，依赖之前数据
     *
     * @param interfaceName
     */
    public void uploadInterfaceData(String interfaceName) {
        //先查出之前的最新数据，在此基础上生成最新数据
    }

    /**
     * 数据无关，直接上传最新数据对象
     *
     * @param data
     */
    public void uploadExecutorsData(ExecutorsData data) {

    }

    /**
     * sql执行次数统计和平均耗时依赖之前数据
     *
     * @param sql
     * @param time
     */
    public void uploadSqlData(String sql, long time) {
        //先查出之前的最新数据，在此基础上生成最新数据
    }


    /**
     * 创建快照对象
     *
     * @return
     */
    public Snapshot createSnapshot() {
        Snapshot snapshot = new DataSnapshot(this);
        return snapshot;
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

        public DataCenter build() {
            return new DataCenter(this);
        }
    }

    /**
     * 快照,定义为内部类是为了只提供宽接口给数据中心，作用于项目重启时构造数据中心
     */
    public static class DataSnapshot implements Snapshot {
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
