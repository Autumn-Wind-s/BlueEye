package blueeye.decompression;

import blueeye.pojo.metric.MetricData;
import blueeye.pojo.metric.Tag.TagData;
import blueeye.pojo.metric.dataSourece.DataSourceData;
import blueeye.pojo.metric.executor.ExecutorData;
import blueeye.pojo.metric.intf.InterfaceData;
import blueeye.pojo.metric.sql.SqlData;
import blueeye.pojo.metric.system.*;
import blueeye.pojo.po.MetricDataPo;
import blueeye.rdb.KryoUtil;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * @Author SDJin
 * @CreationDate 2023/6/19 20:45
 * @Description ：
 */
public class MetricDataDecompressor {


    /**
     * 类型编号与反压缩方法名的映射
     */
    private final static HashMap<Integer,String> MAPPING = new HashMap<>();
    static {
        MAPPING.put(1,"decompressDataSource");
        MAPPING.put(2,"decompressExecutor");
        MAPPING.put(3,"decompressInterface");
        MAPPING.put(4,"decompressSql");
        MAPPING.put(5,"decompressTag");
        MAPPING.put(6,"decompressCpu");
        MAPPING.put(7,"decompressGpu");
        MAPPING.put(8,"decompressGc");
        MAPPING.put(9,"decompressHardDisk");
        MAPPING.put(10,"decompressHeap");
        MAPPING.put(11,"decompressJvmThread");
        MAPPING.put(12,"decompressLoad");
        MAPPING.put(13,"decompressMemory");
        MAPPING.put(14,"decompressNoHeap");
    }

    /**
     * 作为反压缩的入口
     * @param dataPo 对象
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static MetricData decompress(MetricDataPo dataPo) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String methodName=MAPPING.get(dataPo.getDataType());
        return (MetricData) MetricDataDecompressor.class.getDeclaredMethod(methodName, dataPo.getClass()).invoke(null,dataPo);
    }


    /**
     * 作为反压缩入口
     * @param dataPos 对象
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static List<? extends MetricData> decompress(List<MetricDataPo> dataPos) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if(dataPos==null||dataPos.size()==0){
            return new ArrayList<MetricData>();
        }
        String methodName=MAPPING.get(dataPos.get(0).getDataType());
        return (List<? extends MetricData>) MetricDataDecompressor.class.getDeclaredMethod(methodName, List.class).invoke(null,dataPos);
    }
    /**
     * 反压缩dataPo获得数据源数据对象
     *
     * @param dataPo dataPo对象
     * @return 数据源数据对象
     */
    private static DataSourceData decompressDataSource(MetricDataPo dataPo) {
        DataSourceData data = new DataSourceData();
        setCommon(data, dataPo);
        String[] split = dataPo.getJson().split(",");
        data.setDatabaseName(split[0]);
        data.setDatabaseType(split[1]);
        data.setUsername(split[2]);
        data.setPassword(split[3]);
        data.setUrl(split[4]);
        data.setDriverClass(split[5]);
        data.setInitialPoolSize(Integer.parseInt(split[6]));
        data.setMaxPoolSize(Integer.parseInt(split[7]));
        data.setMinPoolSize(Integer.parseInt(split[8]));
        data.setReadOnly(Boolean.parseBoolean(split[9]));
        data.setDefaultTransactionIsolation(Integer.parseInt(split[10]));
        data.setAutoCommit(Boolean.parseBoolean(split[11]));
        data.setThreadsAwaiting(Integer.parseInt(split[12]));
        data.setIleConnections(Integer.parseInt(split[13]));
        data.setBusyConnections(Integer.parseInt(split[14]));
        data.setConnections(Integer.parseInt(split[15]));
        return data;
    }

    private static List<DataSourceData> decompressDataSource(List<MetricDataPo> dataPos) {
        List<DataSourceData> dataSourceData = new ArrayList<>();
        for (MetricDataPo dataPo : dataPos) {
            dataSourceData.add(decompressDataSource(dataPo));
        }
        return dataSourceData;
    }

    /**
     * 反压缩dataPo获得线程池数据对象
     *
     * @param dataPo dataPo对象
     * @return 线程池数据对象
     */
    private static ExecutorData decompressExecutor(MetricDataPo dataPo) {
        ExecutorData executorData = new ExecutorData();
        setCommon(executorData, dataPo);
        String[] split = dataPo.getJson().split(",");
        executorData.setCurrentUsage(Double.parseDouble(split[0]));
        executorData.setPeakUsage(Double.parseDouble(split[1]));
        executorData.setCorePoolSize(Integer.parseInt(split[2]));
        executorData.setMaxPoolSize(Integer.parseInt(split[3]));
        executorData.setCurrentCount(Integer.parseInt(split[4]));
        executorData.setActiveCount(Integer.parseInt(split[5]));
        executorData.setLargestCount(Integer.parseInt(split[6]));
        executorData.setBlockQueueSize(Integer.parseInt(split[7]));
        executorData.setQueueOccupiedSize(Integer.parseInt(split[8]));
        executorData.setQueueSurplusSize(Integer.parseInt(split[9]));
        executorData.setCompletedTaskCount(Long.parseLong(split[10]));
        return executorData;
    }

    private static List<ExecutorData> decompressExecutor(List<MetricDataPo> dataPos) {
        List<ExecutorData> executorData = new ArrayList<>();
        for (MetricDataPo dataPo : dataPos) {
            executorData.add(decompressExecutor(dataPo));
        }
        return executorData;
    }

    /**
     * 反压缩dataPo获得接口数据对象
     *
     * @param dataPo dataPo对象
     * @return 接口数据对象
     */
    private static InterfaceData decompressInterface(MetricDataPo dataPo) {
        InterfaceData data = new InterfaceData();
        setCommon(data, dataPo);
        String[] split = dataPo.getJson().split(",");
        data.setTotalCount(Long.parseLong(split[0]));
        data.setSuccessCount(Long.parseLong(split[1]));
        data.setFailCount(Long.parseLong(split[2]));
        data.setAverageComputingTime(Long.parseLong(split[3]));
        data.setMaxComputingTime(Long.parseLong(split[4]));
        data.setMinComputingTime(Long.parseLong(split[4]));
        return data;
    }

    private List<InterfaceData> decompressInterface(List<MetricDataPo> dataPos) {
        List<InterfaceData> interfaceData = new ArrayList<>();
        for (MetricDataPo dataPo : dataPos) {
            interfaceData.add(decompressInterface(dataPo));
        }
        return interfaceData;
    }

    /**
     * 反压缩dataPo获得sql数据对象
     *
     * @param dataPo dataPo对象
     * @return sql数据对象
     */
    private static SqlData decompressSql(MetricDataPo dataPo) {
        SqlData sqlData = new SqlData();
        setCommon(sqlData, dataPo);
        String[] split = dataPo.getJson().split(",");
        sqlData.setSql(split[0]);
        sqlData.setExecuteCount(Long.parseLong(split[1]));
        sqlData.setAverageComputingTime(Long.parseLong(split[2]));
        sqlData.setRecentComputingTime(Long.parseLong(split[3]));
        return sqlData;
    }

    private static List<SqlData> decompressSql(List<MetricDataPo> dataPos) {
        List<SqlData> sqlData = new ArrayList<>();
        for (MetricDataPo dataPo : dataPos) {
            sqlData.add(decompressSql(dataPo));
        }
        return sqlData;
    }

    /**
     * 反压缩dataPo获得sql数据对象
     *
     * @param dataPo dataPo对象
     * @return sql数据对象
     */
    private static TagData decompressTag(MetricDataPo dataPo) {
        TagData tagData = new TagData();
        setCommon(tagData, dataPo);
        tagData.setData(KryoUtil.readFromString(dataPo.getJson()));
        return tagData;
    }

    private  static List<TagData> decompressTag(List<MetricDataPo> dataPos) {
        List<TagData> tagData = new ArrayList<>();
        for (MetricDataPo dataPo : dataPos) {
            tagData.add(decompressTag(dataPo));
        }
        return tagData;
    }

    /**
     * 反压缩dataPo获得cpu数据对象
     *
     * @param dataPo dataPo对象
     * @return cpu数据对象
     */
    private  static CpuData decompressCpu(MetricDataPo dataPo) {
        CpuData data = new CpuData();
        setCommon(data, dataPo);
        String[] split = dataPo.getJson().split(",");
        data.setUsageRate(Double.parseDouble(split[0]));
        data.setTemperature(Double.parseDouble(split[1]));
        return data;
    }

    private static List<CpuData> decompressCpu(List<MetricDataPo> dataPos) {
        List<CpuData> tagData = new ArrayList<>();
        for (MetricDataPo dataPo : dataPos) {
            tagData.add(decompressCpu(dataPo));
        }
        return tagData;
    }

    /**
     * 反压缩dataPo获得Gpu数据对象
     *
     * @param dataPo dataPo对象
     * @return Gpu数据对象
     */
    private static GpuData decompressGpu(MetricDataPo dataPo) {
        GpuData data = new GpuData();
        setCommon(data, dataPo);
        String[] split = dataPo.getJson().split(",");
        data.setUsageRate(Double.parseDouble(split[0]));
        data.setTemperature(Double.parseDouble(split[1]));
        return data;
    }

    private static List<GpuData> decompressGpu(List<MetricDataPo> dataPos) {
        List<GpuData> tagData = new ArrayList<>();
        for (MetricDataPo dataPo : dataPos) {
            tagData.add(decompressGpu(dataPo));
        }
        return tagData;
    }

    /**
     * 反压缩dataPo获得Gc数据对象
     *
     * @param dataPo dataPo对象
     * @return Gc数据对象
     */
    private static GcData decompressGc(MetricDataPo dataPo) {
        GcData data = new GcData();
        setCommon(data, dataPo);
        String[] split = dataPo.getJson().split(",");
        data.setYongGcFrequency(Long.parseLong(split[0]));
        data.setYongGcTime(Double.parseDouble(split[1]));
        data.setFullGcFrequency(Long.parseLong(split[2]));
        data.setFullGcTime(Double.parseDouble(split[3]));
        return data;
    }

    private static List<GcData> decompressGc(List<MetricDataPo> dataPos) {
        List<GcData> tagData = new ArrayList<>();
        for (MetricDataPo dataPo : dataPos) {
            tagData.add(decompressGc(dataPo));
        }
        return tagData;
    }

    /**
     * 反压缩dataPo获得HardDisk数据对象
     *
     * @param dataPo dataPo对象
     * @return HardDisk数据对象
     */
    private static HardDiskData decompressHardDisk(MetricDataPo dataPo) {
        HardDiskData data = new HardDiskData();
        setCommon(data, dataPo);
        String[] split = dataPo.getJson().split(",");
        data.setTotal(Long.parseLong(split[0]));
        data.setUsage(Long.parseLong(split[1]));
        data.setUsageRate(Double.parseDouble(split[2]));
        return data;
    }

    private static List<HardDiskData> decompressHardDisk(List<MetricDataPo> dataPos) {
        List<HardDiskData> hardDiskData = new ArrayList<>();
        for (MetricDataPo dataPo : dataPos) {
            hardDiskData.add(decompressHardDisk(dataPo));
        }
        return hardDiskData;
    }

    /**
     * 反压缩dataPo获得Heap数据对象
     *
     * @param dataPo dataPo对象
     * @return Heap数据对象
     */
    private static HeapData decompressHeap(MetricDataPo dataPo) {
        HeapData data = new HeapData();
        setCommon(data, dataPo);
        String[] split = dataPo.getJson().split(",");
        data.setMax(Long.parseLong(split[0]));
        data.setTotal(Long.parseLong(split[1]));
        data.setUsage(Long.parseLong(split[2]));
        data.setOld(Long.parseLong(split[3]));
        data.setSurvivor0(Long.parseLong(split[4]));
        data.setSurvivor1(Long.parseLong(split[5]));
        data.setEden(Long.parseLong(split[6]));
        data.setMetSpace(Long.parseLong(split[7]));
        return data;
    }

    private static List<HeapData> decompressHeap(List<MetricDataPo> dataPos) {
        List<HeapData> heapData = new ArrayList<>();
        for (MetricDataPo dataPo : dataPos) {
            heapData.add(decompressHeap(dataPo));
        }
        return heapData;
    }

    /**
     * 反压缩dataPo获得JvmThread数据对象
     *
     * @param dataPo dataPo对象
     * @return JvmThread数据对象
     */
    private static JvmThreadData decompressJvmThread(MetricDataPo dataPo) {
        JvmThreadData data = new JvmThreadData();
        setCommon(data, dataPo);
        String[] split = dataPo.getJson().split(",");
        data.setTotal(Integer.parseInt(split[0]));
        data.setRunnable(Integer.parseInt(split[1]));
        data.setBlocked(Integer.parseInt(split[2]));
        data.setWaiting(Integer.parseInt(split[3]));
        data.setTimeWaiting(Integer.parseInt(split[4]));
        return data;
    }

    private static List<JvmThreadData> decompressJvmThread(List<MetricDataPo> dataPos) {
        List<JvmThreadData> heapData = new ArrayList<>();
        for (MetricDataPo dataPo : dataPos) {
            heapData.add(decompressJvmThread(dataPo));
        }
        return heapData;
    }

    /**
     * 反压缩dataPo获得Load数据对象
     *
     * @param dataPo dataPo对象
     * @return Load数据对象
     */
    private static LoadData decompressLoad(MetricDataPo dataPo) {
        LoadData data = new LoadData();
        setCommon(data, dataPo);
        String[] split = dataPo.getJson().split(",");
        data.setOneMinute(Double.parseDouble(split[0]));
        data.setFiveMinute(Double.parseDouble(split[1]));
        data.setFifteenMinute(Double.parseDouble(split[2]));
        return data;
    }

    private static List<LoadData> decompressLoad(List<MetricDataPo> dataPos) {
        List<LoadData> heapData = new ArrayList<>();
        for (MetricDataPo dataPo : dataPos) {
            heapData.add(decompressLoad(dataPo));
        }
        return heapData;
    }

    /**
     * 反压缩dataPo获得Memory数据对象
     *
     * @param dataPo dataPo对象
     * @return Memory数据对象
     */
    private static MemoryData decompressMemory(MetricDataPo dataPo) {
        MemoryData data = new MemoryData();
        setCommon(data, dataPo);
        String[] split = dataPo.getJson().split(",");
        data.setTotal(Long.parseLong(split[0]));
        data.setUsage(Long.parseLong(split[1]));
        data.setUsageRate(Double.parseDouble(split[2]));
        return data;
    }

    private static List<MemoryData> decompressMemory(List<MetricDataPo> dataPos) {
        List<MemoryData> heapData = new ArrayList<>();
        for (MetricDataPo dataPo : dataPos) {
            heapData.add(decompressMemory(dataPo));
        }
        return heapData;
    }

    /**
     * 反压缩dataPo获得NoHeap数据对象
     *
     * @param dataPo dataPo对象
     * @return NoHeap数据对象
     */
    private static NoHeapData decompressNoHeap(MetricDataPo dataPo) {
        NoHeapData data = new NoHeapData();
        setCommon(data, dataPo);
        String[] split = dataPo.getJson().split(",");
        data.setInit(Long.parseLong(split[0]));
        data.setMax(Long.parseLong(split[1]));
        data.setUsage(Long.parseLong(split[2]));
        data.setUsable(Long.parseLong(split[3]));
        return data;
    }

    private static List<NoHeapData> decompressNoHeap(List<MetricDataPo> dataPos) {
        List<NoHeapData> heapData = new ArrayList<>();
        for (MetricDataPo dataPo : dataPos) {
            heapData.add(decompressNoHeap(dataPo));
        }
        return heapData;
    }

    /**
     * 设置公共属性
     *
     * @param data
     * @param metricDataPo
     */
    private static void setCommon(MetricData data, MetricDataPo metricDataPo) {
        data.setId(metricDataPo.getId());
        data.setTaskId(metricDataPo.getTaskId());
        data.setInstanceId(metricDataPo.getInstanceId());
        data.setCreateTime(metricDataPo.getCreateTime());
    }
}
