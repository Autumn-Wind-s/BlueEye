package blueeye.compression;

import blueeye.pojo.metric.MetricData;
import blueeye.pojo.metric.Tag.TagData;
import blueeye.pojo.metric.dataSourece.DataSourceData;
import blueeye.pojo.metric.executor.ExecutorData;
import blueeye.pojo.metric.intf.InterfaceData;
import blueeye.pojo.metric.sql.SqlData;
import blueeye.pojo.metric.system.*;
import blueeye.pojo.po.MetricDataPo;
import blueeye.util.KryoUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author SDJin
 * @CreationDate 2023/6/19 20:45
 * @Description ：
 */
public class MetricDataCompressor {
    /**
     * 数据类型与压缩方法名的映射
     */
    private final static HashMap<Class, String> MAPPING = new HashMap<>();

    static {
        MAPPING.put(DataSourceData.class, "compressDataSource");
        MAPPING.put(ExecutorData.class, "compressExecutor");
        MAPPING.put(InterfaceData.class, "compressInterface");
        MAPPING.put(SqlData.class, "compressSql");
        MAPPING.put(TagData.class, "compressTag");
        MAPPING.put(CpuData.class, "compressCpu");
        MAPPING.put(GpuData.class, "compressGpu");
        MAPPING.put(GcData.class, "compressGc");
        MAPPING.put(HardDiskData.class, "compressHardDisk");
        MAPPING.put(HeapData.class, "compressHeap");
        MAPPING.put(JvmThreadData.class, "compressJvmThread");
        MAPPING.put(LoadData.class, "compressLoad");
        MAPPING.put(MemoryData.class, "compressMemory");
        MAPPING.put(NoHeapData.class, "compressNoHeap");
    }

    /**
     * 作为压缩的入口
     *
     * @param data 待压缩的数据对象
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static MetricDataPo compress(MetricData data) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<? extends MetricData> aClass = data.getClass();
        String methodName = MAPPING.get(aClass);
        Object invoke = MetricDataCompressor.class.getDeclaredMethod(methodName, aClass).invoke(null, data);
        return (MetricDataPo) invoke;
    }

    /**
     * 作为压缩的入口
     *
     * @param data 待压缩的数据对象
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static List<MetricDataPo> compress(List<? extends MetricData> data) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (data.size() == 0) {
            return null;
        }
        Class<? extends MetricData> aClass = data.get(0).getClass();
        String methodName = MAPPING.get(aClass);
        Object invoke = MetricDataCompressor.class.getDeclaredMethod(methodName, List.class).invoke(null, data);
        return (List<MetricDataPo>) invoke;
    }

    /**
     * 压缩数据源数据
     *
     * @param data 数据源数据对象
     * @return 压缩后的MetricDataPo对象
     */
    private static MetricDataPo compressDataSource(DataSourceData data) {
        MetricDataPo metricDataPo = new MetricDataPo(data.getId(), data.getTaskId(), data.getInstanceId(), data.getCreateTime(), 1);
        String json = data.getDatabaseName() + "," + data.getDatabaseType() + "," + data.getUsername() + "," + data.getPassword() + "," + data.getUrl() + "," + data.getDriverClass() + "," + data.getInitialPoolSize() + ","
                + data.getMaxPoolSize() + "," + data.getMinPoolSize() + "," + data.getReadOnly() + "," + data.getDefaultTransactionIsolation() + "," + data.getAutoCommit() + "," + data.getThreadsAwaiting() + "," + data.getIleConnections() + "," + data.getBusyConnections() + "," + data.getConnections();
        metricDataPo.setJson(json);
        return metricDataPo;
    }

    private static List<MetricDataPo> compressDataSource(List<DataSourceData> data) {
        List<MetricDataPo> metricDataPos = new ArrayList<>();
        for (DataSourceData datum : data) {
            metricDataPos.add(compressDataSource(datum));
        }
        return metricDataPos;
    }

    /**
     * 压缩线程池数据
     *
     * @param data 线程池数据对象
     * @return 压缩后的MetricDataPo对象
     */
    private static MetricDataPo compressExecutor(ExecutorData data) {
        MetricDataPo metricDataPo = new MetricDataPo(data.getId(), data.getTaskId(), data.getInstanceId(), data.getCreateTime(), 2);
        String json = data.getCurrentUsage() + "," + data.getPeakUsage() + "," + data.getCorePoolSize() + "," + data.getMaxPoolSize() + "," + data.getCurrentCount() + "," + data.getActiveCount() + "," + data.getLargestCount() + "," + data.getBlockQueueSize() + "," + data.getQueueOccupiedSize() + "," + data.getQueueSurplusSize() + "," + data.getCompletedTaskCount();
        metricDataPo.setJson(json);
        return metricDataPo;
    }

    private static List<MetricDataPo> compressExecutor(List<ExecutorData> data) {
        List<MetricDataPo> metricDataPos = new ArrayList<>();
        for (ExecutorData datum : data) {
            metricDataPos.add(compressExecutor(datum));
        }
        return metricDataPos;
    }

    /**
     * 压缩接口数据
     *
     * @param data 接口数据对象
     * @return 压缩后的MetricDataPo对象
     */
    private static MetricDataPo compressInterface(InterfaceData data) {
        MetricDataPo metricDataPo = new MetricDataPo(data.getId(), data.getTaskId(), data.getInstanceId(), data.getCreateTime(), 3);
        String json = data.getTotalCount() + "," + data.getSuccessCount() + "," + data.getFailCount() + "," + data.getAverageComputingTime() + "," + data.getMaxComputingTime() + "," + data.getMinComputingTime();
        metricDataPo.setJson(json);
        return metricDataPo;
    }

    private static List<MetricDataPo> compressInterface(List<InterfaceData> data) {
        List<MetricDataPo> metricDataPos = new ArrayList<>();
        for (InterfaceData datum : data) {
            metricDataPos.add(compressInterface(datum));
        }
        return metricDataPos;
    }

    /**
     * 压缩sql数据
     *
     * @param data sql数据对象
     * @return 压缩后的MetricDataPo对象
     */
    private static MetricDataPo compressSql(SqlData data) {
        MetricDataPo metricDataPo = new MetricDataPo(data.getId(), data.getTaskId(), data.getInstanceId(), data.getCreateTime(), 4);
        String json = data.getSql() + "," + data.getExecuteCount() + "," + data.getAverageComputingTime() + "," + data.getRecentComputingTime();
        metricDataPo.setJson(json);
        return metricDataPo;
    }

    private static List<MetricDataPo> compressSql(List<SqlData> data) {
        List<MetricDataPo> metricDataPos = new ArrayList<>();
        for (SqlData datum : data) {
            metricDataPos.add(compressSql(datum));
        }
        return metricDataPos;
    }

    /**
     * 压缩Tag数据
     *
     * @param data Tag数据对象
     * @return 压缩后的MetricDataPo对象
     */
    private static MetricDataPo compressTag(TagData data) {
        MetricDataPo metricDataPo = new MetricDataPo(data.getId(), data.getTaskId(), data.getInstanceId(), data.getCreateTime(), 5);
        String json = KryoUtil.writeToString(data.getData());
        metricDataPo.setJson(json);
        return metricDataPo;
    }

    private static List<MetricDataPo> compressTag(List<TagData> data) {
        List<MetricDataPo> metricDataPos = new ArrayList<>();
        for (TagData datum : data) {
            metricDataPos.add(compressTag(datum));
        }
        return metricDataPos;
    }

    /**
     * 压缩cpu数据
     *
     * @param data cpu数据对象
     * @return 压缩后的MetricDataPo对象
     */
    private  static MetricDataPo compressCpu(CpuData data) {
        MetricDataPo metricDataPo = new MetricDataPo(data.getId(), data.getTaskId(), data.getInstanceId(), data.getCreateTime(), 6);
        String json = data.getUsageRate() + "," + data.getTemperature();
        metricDataPo.setJson(json);
        return metricDataPo;
    }

    private static List<MetricDataPo> compressCpu(List<CpuData> data) {
        List<MetricDataPo> metricDataPos = new ArrayList<>();
        for (CpuData datum : data) {
            metricDataPos.add(compressCpu(datum));
        }
        return metricDataPos;
    }

    /**
     * 压缩Gpu数据
     *
     * @param data Gpu数据对象
     * @return 压缩后的MetricDataPo对象
     */
    private static MetricDataPo compressGpu(GpuData data) {
        MetricDataPo metricDataPo = new MetricDataPo(data.getId(), data.getTaskId(), data.getInstanceId(), data.getCreateTime(), 7);
        String json = data.getUsageRate() + "," + data.getTemperature();
        metricDataPo.setJson(json);
        return metricDataPo;
    }

    private static List<MetricDataPo> compressGpu(List<GpuData> data) {
        List<MetricDataPo> metricDataPos = new ArrayList<>();
        for (GpuData datum : data) {
            metricDataPos.add(compressGpu(datum));
        }
        return metricDataPos;
    }

    /**
     * 压缩Gc数据
     *
     * @param data Gc数据对象
     * @return 压缩后的MetricDataPo对象
     */
    private static MetricDataPo compressGc(GcData data) {
        MetricDataPo metricDataPo = new MetricDataPo(data.getId(), data.getTaskId(), data.getInstanceId(), data.getCreateTime(), 8);
        String json = data.getYongGcFrequency() + "," + data.getYongGcTime() + "," + data.getFullGcFrequency() + "," + data.getFullGcTime();
        metricDataPo.setJson(json);
        return metricDataPo;
    }

    private static List<MetricDataPo> compressGc(List<GcData> data) {
        List<MetricDataPo> metricDataPos = new ArrayList<>();
        for (GcData datum : data) {
            metricDataPos.add(compressGc(datum));
        }
        return metricDataPos;
    }

    /**
     * 压缩HardDisk数据
     *
     * @param data HardDisk数据对象
     * @return 压缩后的MetricDataPo对象
     */
    private static MetricDataPo compressHardDisk(HardDiskData data) {
        MetricDataPo metricDataPo = new MetricDataPo(data.getId(), data.getTaskId(), data.getInstanceId(), data.getCreateTime(), 9);
        String json = data.getTotal() + "," + data.getUsage() + "," + data.getUsageRate();
        metricDataPo.setJson(json);
        return metricDataPo;
    }

    private static List<MetricDataPo> compressHardDisk(List<HardDiskData> data) {
        List<MetricDataPo> metricDataPos = new ArrayList<>();
        for (HardDiskData datum : data) {
            metricDataPos.add(compressHardDisk(datum));
        }
        return metricDataPos;
    }

    /**
     * 压缩Heap数据
     *
     * @param data Heap数据对象
     * @return 压缩后的MetricDataPo对象
     */
    private static MetricDataPo compressHeap(HeapData data) {
        MetricDataPo metricDataPo = new MetricDataPo(data.getId(), data.getTaskId(), data.getInstanceId(), data.getCreateTime(), 10);
        String json = data.getMax() + "," + data.getTotal() + "," + data.getUsage() + "," + data.getOld() + "," + data.getSurvivor0() + "," + data.getSurvivor1()
                + "," + data.getEden()+","+ data.getMetSpace();
        metricDataPo.setJson(json);
        return metricDataPo;
    }

    private static List<MetricDataPo> compressHeap(List<HeapData> data) {
        List<MetricDataPo> metricDataPos = new ArrayList<>();
        for (HeapData datum : data) {
            metricDataPos.add(compressHeap(datum));
        }
        return metricDataPos;
    }

    /**
     * 压缩JvmThread数据
     *
     * @param data JvmThread数据对象
     * @return 压缩后的MetricDataPo对象
     */
    private static MetricDataPo compressJvmThread(JvmThreadData data) {
        MetricDataPo metricDataPo = new MetricDataPo(data.getId(), data.getTaskId(), data.getInstanceId(), data.getCreateTime(), 11);
        String json = data.getTotal() + "," + data.getRunnable() + "," + data.getBlocked() + "," + data.getWaiting() + "," + data.getTimeWaiting();
        metricDataPo.setJson(json);
        return metricDataPo;
    }

    private static List<MetricDataPo> compressJvmThread(List<JvmThreadData> data) {
        List<MetricDataPo> metricDataPos = new ArrayList<>();
        for (JvmThreadData datum : data) {
            metricDataPos.add(compressJvmThread(datum));
        }
        return metricDataPos;
    }

    /**
     * 压缩Load数据
     *
     * @param data Load数据对象
     * @return 压缩后的MetricDataPo对象
     */
    private  static MetricDataPo compressLoad(LoadData data) {
        MetricDataPo metricDataPo = new MetricDataPo(data.getId(), data.getTaskId(), data.getInstanceId(), data.getCreateTime(), 12);
        String json = data.getOneMinute() + "," + data.getFiveMinute() + "," + data.getFifteenMinute();
        metricDataPo.setJson(json);
        return metricDataPo;
    }

    private static List<MetricDataPo> compressLoad(List<LoadData> data) {
        List<MetricDataPo> metricDataPos = new ArrayList<>();
        for (LoadData datum : data) {
            metricDataPos.add(compressLoad(datum));
        }
        return metricDataPos;
    }

    /**
     * 压缩Memory数据
     *
     * @param data Memory数据对象
     * @return 压缩后的MetricDataPo对象
     */
    private static MetricDataPo compressMemory(MemoryData data) {
        MetricDataPo metricDataPo = new MetricDataPo(data.getId(), data.getTaskId(), data.getInstanceId(), data.getCreateTime(), 13);
        String json = data.getTotal() + "," + data.getUsage() + "," + data.getUsageRate();
        metricDataPo.setJson(json);
        return metricDataPo;
    }

    private static List<MetricDataPo> compressMemory(List<MemoryData> data) {
        List<MetricDataPo> metricDataPos = new ArrayList<>();
        for (MemoryData datum : data) {
            metricDataPos.add(compressMemory(datum));
        }
        return metricDataPos;
    }

    /**
     * 压缩NoHeap数据
     *
     * @param data NoHeap数据对象
     * @return 压缩后的MetricDataPo对象
     */
    private static MetricDataPo compressNoHeap(NoHeapData data) {
        MetricDataPo metricDataPo = new MetricDataPo(data.getId(), data.getTaskId(), data.getInstanceId(), data.getCreateTime(), 14);
        String json = data.getInit() + "," + data.getMax() + "," + data.getUsage() + "," + data.getUsable();
        metricDataPo.setJson(json);
        return metricDataPo;
    }

    private static List<MetricDataPo> compressNoHeap(List<NoHeapData> data) {
        List<MetricDataPo> metricDataPos = new ArrayList<>();
        for (NoHeapData datum : data) {
            metricDataPos.add(compressNoHeap(datum));
        }
        return metricDataPos;
    }


}
