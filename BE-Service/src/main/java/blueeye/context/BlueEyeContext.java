package blueeye.context;

import blueeye.annotation.RequestEye;
import blueeye.center.DataCenter;
import blueeye.compression.AlertRecordCompressor;
import blueeye.compression.MetricDataCompressor;
import blueeye.compression.TaskCompressor;
import blueeye.compression.TaskInstanceCompressor;
import blueeye.config.AlertConfig;
import blueeye.config.BlueEyeConfig;
import blueeye.consumer.AlertTaskConsumer;
import blueeye.container.*;
import blueeye.dispatch.TimerScheduler;
import blueeye.email.MailSender;
import blueeye.filter.RequestFilter;
import blueeye.mapping.MetricTaskIdMapping;
import blueeye.note.NoteSender;
import blueeye.persistent.*;
import blueeye.persistent.defaultImpl.JdbcMapperManager;
import blueeye.pojo.alert.AlertRecord;
import blueeye.pojo.alert.AlertRule;
import blueeye.pojo.instance.InstanceState;
import blueeye.pojo.instance.TaskInstance;
import blueeye.pojo.metric.MetricData;
import blueeye.pojo.metric.MetricsType;
import blueeye.pojo.metric.dataSourece.DataSourceData;
import blueeye.pojo.metric.executor.ExecutorData;
import blueeye.pojo.metric.system.*;
import blueeye.pojo.po.AlertRecordPo;
import blueeye.pojo.po.MetricDataPo;
import blueeye.pojo.po.TaskInstancePo;
import blueeye.pojo.po.TaskPo;
import blueeye.pojo.task.impl.TimerTask;
import blueeye.pojo.task.impl.alert.AlertTask;
import blueeye.pojo.task.impl.intf.InterfaceTask;
import blueeye.pojo.task.impl.monitor.ExecuteCallback;
import blueeye.pojo.task.impl.monitor.MonitorTask;
import blueeye.pojo.task.impl.script.ScriptTask;
import blueeye.pojo.user.User;
import blueeye.rdb.Rdb;
import blueeye.rdb.Snapshot;
import blueeye.service.AlertRecordService;
import blueeye.service.InstanceService;
import blueeye.service.MetricDataService;
import blueeye.service.TaskService;
import blueeye.util.*;
import blueeye.wx.WxMsSender;
import com.alibaba.druid.pool.DruidDataSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import oshi.util.GlobalConfig;


import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @Author SDJin
 * @CreationDate 2023/2/8 13:47
 * @Description ：
 */
@Slf4j
public class BlueEyeContext {
    public static DataCenter dataCenter;
    public static TimerScheduler timerScheduler;
    public static Rdb rdb;
    public static MapperManager mapperManager;


    /**
     * 整个平台的初始化方法，外部项目需在启动时执行该方法
     *
     * @param context
     * @throws IOException
     * @throws DocumentException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public void init(ServletContext context) throws IOException, DocumentException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, InterruptedException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("blueEye.xml");
        SAXReader sr = new SAXReader();
        Document doc = sr.read(inputStream);
        Element rootElement = doc.getRootElement();
        //判断是否为重启
        Element restartConfig = rootElement.element("restartConfig");
        if ("true".equals(restartConfig.elementText("restart"))) {
            log.info("开始重启");
            //进行重启
            String recoveryFile = restartConfig.elementText("recoveryFile");
            //初始化数据备份/恢复服务，恢复数据中心
            rdb = new Rdb(recoveryFile);
            dataCenter = rdb.getDataCenter();
            System.out.println(dataCenter);
            //初始化持久化数据读写服务,将这四个对象mapper注入到对应四个Service的静态属性上。
            initMapper(dataCenter.getConfig().getBlueEyeconfig());
            //初始化报警服务
            initAlert(dataCenter.getConfig().getAlertConfig());
            //初始化任务调度服务
             timerScheduler = new TimerScheduler(Optional.ofNullable(dataCenter.getConfig().getBlueEyeconfig().getTimeSpan()), Optional.ofNullable(dataCenter.getConfig().getBlueEyeconfig().getWheelSize()), Optional.ofNullable(dataCenter.getConfig().getBlueEyeconfig().getCoreThread()), Optional.ofNullable(dataCenter.getConfig().getBlueEyeconfig().getRetryNum()));
            //恢复实例执行
            List<TaskInstance> activeInstance = dataCenter.getInstances().getActiveInstance();
            for (TaskInstance instance : activeInstance) {
                if(instance.getState().equals(InstanceState.BLOCKING)){
                    timerScheduler.addBlockTask(instance);
                } if (instance.getState().equals(InstanceState.RUNNING)){
                     timerScheduler.getWorkerThreadPool().execute(instance);
                }else {
                    instance.setExecutionTime(new Timestamp(System.currentTimeMillis()+instance.getTask().getCycle()));
                    timerScheduler.add(instance);
                }
            }
            //扫描BlueEye注解
            Set<String> strings = scanRequestEye(dataCenter.getConfig().getBlueEyeconfig().getScanPackageName());
            //设计接口qps监控
            setInterfaceMonitor(context, strings);
        } else {
            //进行初始化
            BlueEyeConfig blueEyeConfig = readBlueEyeConfig(sr, "blueEye.xml");
            //读取报警配置文件
            AlertConfig alertConfig = readAlertConfig(sr, "AlertConfig.xml");
            //创建数据中心
            DataCenter data = new DataCenter.DataCenterBuilder().metaTasks(new TaskContainer()).monitorTasks(new TaskContainer()).interfaceTasks(new TaskContainer()).scriptTasks(new TaskContainer()).metrics(new MetricDataContainer()).instances(new InstanceContainer()).records(new AlertRecordContainer()).mapping(new MetricTaskIdMapping()).taskId(new AtomicInteger()).instanceId(new AtomicInteger()).dataId(new AtomicInteger()).recordId(new AtomicInteger()).config(new ConfigContainer()).build();
            data.getConfig().setAlertConfig(alertConfig);
            data.getConfig().setBlueEyeconfig(blueEyeConfig);
            dataCenter = data;
            //初始化数据备份/恢复服务
            rdb = new Rdb(dataCenter, Optional.of(blueEyeConfig.getRdbPath()), Optional.of(blueEyeConfig.getRdbFileNum()));
            //初始化持久化数据读写服务,将这四个对象mapper注入到对应四个Service的静态属性上。
            initMapper(dataCenter.getConfig().getBlueEyeconfig());
            //初始化报警服务
            initAlert(dataCenter.getConfig().getAlertConfig());
            //初始化任务调度服务
            timerScheduler = new TimerScheduler(Optional.ofNullable(blueEyeConfig.getTimeSpan()), Optional.ofNullable(blueEyeConfig.getWheelSize()), Optional.ofNullable(blueEyeConfig.getCoreThread()), Optional.ofNullable(blueEyeConfig.getRetryNum()));
            //创建元任务
            createDefaultMetaTask();
            log.info("默认元任务初始化结束");
            //创建默认的监控任务
            createDefaultMonitorTask();
            log.info("默认监控初始化结束");
            //扫描BlueEye注解
            Set<String> strings = scanRequestEye(blueEyeConfig.getScanPackageName());
            log.info("注解扫描结束");
            //设计接口qps监控
            setInterfaceMonitor(context,strings);
            log.info("BlueEye初始化结束");


        }

    }

    /**
     * 整个平台的终止方法，外部项目需在终止时执行该方法
     *
     * @throws IOException
     */
    public void destroy() throws IOException {
        //终止任务执行器
        timerScheduler.getWorkerThreadPool().shutdown();
        //终止推进线程
        timerScheduler.getBossThreadPool().shutdownNow();
        //手动备份一次数据
        rdb.backup();
        //释放持久化的资源
        mapperManager.destroy();
    }

    /**
     * 读取BlueEye配置文件
     *
     * @param sr
     * @param name
     * @return
     * @throws FileNotFoundException
     * @throws DocumentException
     */
    public BlueEyeConfig readBlueEyeConfig(SAXReader sr, String name) throws FileNotFoundException, DocumentException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(name);
        Document doc = sr.read(inputStream);
        Element rootElement = doc.getRootElement();

        BlueEyeConfig blueEyeConfig = new BlueEyeConfig();
        //解析用户信息

        blueEyeConfig.setUsers((List<User>) rootElement.element("users").elements().stream().map(object -> {
            Element element = (Element) object;
            User user = new User();
            user.setName(element.elementText("name"));
            user.setPassword(element.elementText("password"));
            user.setRole(Integer.parseInt(element.elementText("role")));
            return user;
        }).collect(Collectors.toList()));
        //解析requestEye扫描路径
        blueEyeConfig.setScanPackageName(rootElement.elementText("scanPackageName"));
        //解析rdb配置
        blueEyeConfig.setRdbPath(rootElement.element("rdb").elementText("path"));
        blueEyeConfig.setRdbFileNum(Integer.parseInt(rootElement.element("rdb").elementText("fileNum")));
        //解析持久化配置
        Element persistence = rootElement.element("persistence");
        blueEyeConfig.setProperties(persistence.elementText("properties"));
        if ("default".equals(persistence.elementText("mode"))) {
            blueEyeConfig.setManager("blueeye.persistent.defaultImpl.JdbcMapperManager");
            blueEyeConfig.setAlertMapper("blueeye.persistent.defaultImpl.JdbcAlertRecordMapper");
            blueEyeConfig.setInstanceMapper("blueeye.persistent.defaultImpl.JdbcInstanceMapper");
            blueEyeConfig.setMetricMapper("blueeye.persistent.defaultImpl.JdbcMetricDataMapper");
            blueEyeConfig.setTaskMapper("blueeye.persistent.defaultImpl.JdbcTaskMapper");
        } else {
            blueEyeConfig.setManager(persistence.elementText("manager"));
            blueEyeConfig.setAlertMapper(persistence.elementText("alertMapper"));
            blueEyeConfig.setInstanceMapper(persistence.elementText("instanceMapper"));
            blueEyeConfig.setMetricMapper(persistence.elementText("metricMapper"));
            blueEyeConfig.setTaskMapper(persistence.elementText("taskMapper"));
        }
        //解析时间轮配置
        blueEyeConfig.setTimeSpan(Long.parseLong(rootElement.element("timeWheel").elementText("timeSpan")));
        blueEyeConfig.setWheelSize(Integer.parseInt(rootElement.element("timeWheel").elementText("size")));
        //解析任务执行器配置
        blueEyeConfig.setCoreThread(Integer.parseInt(rootElement.elementText("coreThread")));
        //解析实例重试次数
        blueEyeConfig.setRetryNum(Integer.parseInt(rootElement.elementText("retryNum")));
        System.out.println(blueEyeConfig);
        return blueEyeConfig;
    }

    /**
     * 读取报警配置文件
     *
     * @param sr
     * @param name
     * @return
     * @throws DocumentException
     * @throws FileNotFoundException
     */
    public AlertConfig readAlertConfig(SAXReader sr, String name) throws DocumentException, FileNotFoundException {
        InputStream alertFile = this.getClass().getClassLoader().getResourceAsStream(name);
        Document alert = sr.read(alertFile);
        AlertConfig alertConfig = new AlertConfig();
        Element root = alert.getRootElement();
        Element wx = root.element("wx");
        alertConfig.setWxToken(wx.attributeValue("wxToken"));
        alertConfig.setWxAppid(wx.attributeValue("wxAppid"));
        alertConfig.setWxSecret(wx.attributeValue("wxSecret"));
        alertConfig.setWxTemplateId(wx.attributeValue("wxTemplateId"));
        Element note = root.element("note");
        alertConfig.setAccessKey(note.attributeValue("accessKey"));
        alertConfig.setAccessKeySecret(note.attributeValue("accessKeySecret"));
        alertConfig.setSignName(note.attributeValue("signName"));
        alertConfig.setTemplateCode(note.attributeValue("templateCode"));
        Element mail = root.element("mail");
        alertConfig.setMailUser(mail.attributeValue("mailUser"));
        alertConfig.setMailPassword(mail.attributeValue("mailPassword"));
        alertConfig.setQueueCapacity(Integer.parseInt(root.elementText("queueCapacity")));
        return alertConfig;
    }

    /**
     * 创建系统默认的监控任务
     */
    public void createDefaultMonitorTask() {
        addCustomizeTask(Optional.of("cpu监控任务"), Optional.of("负责监控cpu的数据"), new ExecuteCallback() {
            @Override
            public String execute(int taskId, int instanceId) throws Exception {
                CpuData cpuData = new CpuData();
                setCommon(cpuData, taskId, instanceId);
                //获取cpu利用率和温度
                CpuUtil.gatherDta(cpuData);
                //判断数据是否需要报警
                alertJudge(taskId, cpuData, "cpu");
                //上传数据
                dataCenter.getMetrics().addMetricData(cpuData);
                return "cpu数据收集和报警完成";
            }
        }, Optional.ofNullable(null), Optional.of(5000L), Optional.of(1));
        addCustomizeTask(Optional.of("gpu监控任务"), Optional.of("负责监控gpu的数据"), new ExecuteCallback() {
            @Override
            public String execute(int taskId, int instanceId) throws Exception {
                GpuData gpuData = new GpuData();
                setCommon(gpuData, taskId, instanceId);
                //获取gpu利用率和温度
                GpuUtil.gatherData(gpuData);
                //判断数据是否需要报警
                alertJudge(taskId, gpuData, "gpu");
                //上传数据
                dataCenter.getMetrics().addMetricData(gpuData);
                return "Gpu数据收集和报警完成";
            }
        }, Optional.ofNullable(null), Optional.of(10000L), Optional.of(1));
        addCustomizeTask(Optional.of("gc监控任务"), Optional.of("负责监控gc的数据"), new ExecuteCallback() {
            @Override
            public String execute(int taskId, int instanceId) throws Exception {
                GcData gcData = new GcData();
                setCommon(gcData, taskId, instanceId);
                //获取gc相关数据
                GcUtil.gatherData(gcData);
                //判断数据是否需要报警
                alertJudge(taskId, gcData, "gc");
                //上传数据
                dataCenter.getMetrics().addMetricData(gcData);
                return "Gc数据收集和报警完成";
            }
        }, Optional.ofNullable(null), Optional.of(5000L), Optional.of(1));
        addCustomizeTask(Optional.of("hardDisk监控任务"), Optional.of("负责监控hardDisk的数据"), new ExecuteCallback() {
            @Override
            public String execute(int taskId, int instanceId) throws Exception {
                HardDiskData hardDiskData = new HardDiskData();
                setCommon(hardDiskData, taskId, instanceId);
                //获取HardDisk相关数据
                HardDiskUtil.gatherData(hardDiskData);
                //判断数据是否需要报警
                alertJudge(taskId, hardDiskData, "hardDiskData");
                //上传数据
                dataCenter.getMetrics().addMetricData(hardDiskData);
                return "hardDiskData数据收集和报警完成";
            }
        }, Optional.ofNullable(null), Optional.of(5000L), Optional.of(1));
        addCustomizeTask(Optional.of("jvmThread监控任务"), Optional.of("负责监控jvmThread的数据"), new ExecuteCallback() {
            @Override
            public String execute(int taskId, int instanceId) throws Exception {
                JvmThreadData data = new JvmThreadData();
                setCommon(data, taskId, instanceId);
                //获取JvmThread相关数据
                JvmThreadUtil.gatherData(data);
                //判断数据是否需要报警
                alertJudge(taskId, data, "jvmThread");
                //上传数据
                dataCenter.getMetrics().addMetricData(data);
                return "jvmThread数据收集和报警完成";
            }
        }, Optional.ofNullable(null), Optional.of(5000L), Optional.of(1));
        addCustomizeTask(Optional.of("heap监控任务"), Optional.of("负责监控heap的数据"), new ExecuteCallback() {
            @Override
            public String execute(int taskId, int instanceId) throws Exception {
                HeapData data = new HeapData();
                setCommon(data, taskId, instanceId);
                //获取Heap相关数据
                HeapUtil.gatherData(data);
                //判断数据是否需要报警
                alertJudge(taskId, data, "heap");
                //上传数据
                dataCenter.getMetrics().addMetricData(data);
                return "heap数据收集和报警完成";
            }
        }, Optional.ofNullable(null), Optional.of(5000L), Optional.of(1));
        addCustomizeTask(Optional.of("load监控任务"), Optional.of("负责监控系统load的数据"), new ExecuteCallback() {
            @Override
            public String execute(int taskId, int instanceId) throws Exception {
                LoadData data = new LoadData();
                setCommon(data, taskId, instanceId);
                //获取load相关数据
                LoadUtil.gatherData(data);
                //判断数据是否需要报警
                alertJudge(taskId, data, "load");
                //上传数据
                dataCenter.getMetrics().addMetricData(data);
                return "load数据收集和报警完成";
            }
        }, Optional.ofNullable(null), Optional.of(5000L), Optional.of(1));
        addCustomizeTask(Optional.of("memory监控任务"), Optional.of("负责监控系统memory的数据"), new ExecuteCallback() {
            @Override
            public String execute(int taskId, int instanceId) throws Exception {
                MemoryData data = new MemoryData();
                setCommon(data, taskId, instanceId);
                //获取MemoryData相关数据
                MemoryUtil.gatherData(data);
                //判断数据是否需要报警
                alertJudge(taskId, data, "memory");
                //上传数据
                dataCenter.getMetrics().addMetricData(data);
                return "memory数据收集和报警完成";
            }
        }, Optional.ofNullable(null), Optional.of(5000L), Optional.of(1));
        addCustomizeTask(Optional.of("noHeap监控任务"), Optional.of("负责监控系统noHeap的数据"), new ExecuteCallback() {
            @Override
            public String execute(int taskId, int instanceId) throws Exception {
                NoHeapData data = new NoHeapData();
                setCommon(data, taskId, instanceId);
                //获取NoHeap相关数据
                NoHeapUtil.gatherData(data);
                //判断数据是否需要报警
                alertJudge(taskId, data, "noHeap");
                //上传数据
                dataCenter.getMetrics().addMetricData(data);
                return "noHeap数据收集和报警完成";
            }
        }, Optional.ofNullable(null), Optional.of(10000L), Optional.of(1));

    }

    /**
     * 创建系统默认的元任务
     */
    public void createDefaultMetaTask() {
        addMetaTask(Optional.of("过期报警记录淘汰元任务"), Optional.of("负责淘汰内存中已到期报警记录"), new ExecuteCallback() {
            @Override
            public String execute(int taskId, int instanceId) throws Exception {
                //从报警记录容器中删除过期记录
                List<AlertRecord> alertRecords = dataCenter.getRecords().deleteAlertRecordByTime(System.currentTimeMillis() - dataCenter.getConfig().getAlertRecordPersistence().get());
                //将过期记录持久化
                List<AlertRecordPo> compress = AlertRecordCompressor.compress(alertRecords);
                mapperManager.alertRecordMapper.persistence(compress);
                return "报警记录淘汰完成";
            }
        }, Optional.ofNullable(null), Optional.of(3000L), Optional.of(3));
        addMetaTask(Optional.of("过期指标数据淘汰元任务"), Optional.of("负责淘汰内存中已到期指标数据"), new ExecuteCallback() {
            @Override
            public String execute(int taskId, int instanceId) throws Exception {
                for (Map.Entry<Integer, Long> entry : dataCenter.getConfig().getMetricPersistence().entrySet()) {
                    //从数据容器中删除过期数据
                    List<MetricData> metricData = dataCenter.getMetrics().deleteMetricDataByTime(entry.getKey(), System.currentTimeMillis() - entry.getValue());
                    //将过期数据持久化
                    List<MetricDataPo> compress = MetricDataCompressor.compress(metricData);
                    mapperManager.metricDataMapper.persistence(compress);
                }
                return "过期指标数据淘汰完成";
            }
        }, Optional.ofNullable(null), Optional.of(10 * 60 * 1000L), Optional.of(3));
        addMetaTask(Optional.of("过期任务淘汰元任务"), Optional.of("负责淘汰内存中已到期且未有活跃实例的任务"), new ExecuteCallback() {
            @Override
            public String execute(int id, int instanceId) throws Exception {
                //筛选出不活跃且过期任务
                long time = System.currentTimeMillis() - dataCenter.getConfig().getTaskPersistence().get();
                List<Integer> taskIds = dataCenter.getMonitorTasks().getTaskIds();
                List<Integer> list = new ArrayList<>();
                for (Integer taskId : taskIds) {
                    if (!dataCenter.getInstances().contain(taskId) && dataCenter.getMonitorTasks().selectById(taskId).getCreateTime().getTime() <= time) {
                        list.add(taskId);
                    }
                }
                List<Integer> taskIds1 = dataCenter.getInterfaceTasks().getTaskIds();
                List<Integer> list1 = new ArrayList<>();
                for (Integer taskId : taskIds1) {
                    if (!dataCenter.getInstances().contain(taskId) && dataCenter.getInterfaceTasks().selectById(taskId).getCreateTime().getTime() <= time) {
                        list1.add(taskId);
                    }
                }
                List<Integer> taskIds2 = dataCenter.getScriptTasks().getTaskIds();
                List<Integer> list2 = new ArrayList<>();
                for (Integer taskId : taskIds2) {
                    if (!dataCenter.getInstances().contain(taskId) && dataCenter.getScriptTasks().selectById(taskId).getCreateTime().getTime() <= time) {
                        list2.add(taskId);
                    }
                }
                //删除过期任务
                List<MonitorTask> remove = dataCenter.getMonitorTasks().remove(list);
                List<InterfaceTask> remove1 = dataCenter.getInterfaceTasks().remove(list1);
                List<ScriptTask> remove2 = dataCenter.getScriptTasks().remove(list2);
                //将过期任务持久化
                List<TaskPo> compress = TaskCompressor.compress(remove);
                List<TaskPo> compress1 = TaskCompressor.compress(remove1);
                List<TaskPo> compress2 = TaskCompressor.compress(remove2);
                mapperManager.taskMapper.persistence(compress);
                mapperManager.taskMapper.persistence(compress1);
                mapperManager.taskMapper.persistence(compress2);
                return "过期指标数据淘汰完成";
            }
        }, Optional.ofNullable(null), Optional.of(12 * 60 * 60 * 1000L), Optional.of(3));
        addMetaTask(Optional.of("过期实例淘汰元任务"), Optional.of("负责淘汰内存中已到期且状态为已完成或死亡的任务实例"), new ExecuteCallback() {
            @Override
            public String execute(int taskId, int instanceId) throws Exception {
                //筛选出已到期且状态为已完成或死亡的任务实例
                List<TaskInstance> inertiaTaskInstance = dataCenter.getInstances().getInertiaTaskInstance();
                long time = System.currentTimeMillis() - dataCenter.getConfig().getInstancePersistence().get();
                List<TaskInstance> taskInstances = new ArrayList<>();
                for (TaskInstance instance : inertiaTaskInstance) {
                    if (instance.getFinishTime().getTime() <= time) {
                        taskInstances.add(instance);
                    }
                }
                //删除到期实例
                dataCenter.getInstances().remove(taskInstances);
                //持久化到期实例
                List<TaskInstancePo> compress = TaskInstanceCompressor.compress(taskInstances);
                mapperManager.instanceMapper.persistence(compress);
                return "过期实例淘汰完成";
            }
        }, Optional.ofNullable(null), Optional.of(3 * 60 * 1000L), Optional.of(3));
        addCustomizeTask(Optional.of("数据备份元任务"), Optional.of("负责备份内存数据"), new ExecuteCallback() {
            @Override
            public String execute(int taskId, int instanceId) throws Exception {
                rdb.backup();
                return "数据备份完成";
            }
        }, Optional.ofNullable(null), Optional.of(3000L), Optional.of(5));
//        todo 监控系统各组件的元任务
    }

    /**
     * 扫描BlueEye注解
     *
     * @param packageName
     * @return
     */
    public Set<String> scanRequestEye(String packageName) {
        HashSet<String> strings = new HashSet<>();
        Set<Class<?>> classSet = ClassUtil.extractPackageClass(packageName);
        for (Class<?> aClass : classSet) {
            for (Method method : aClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(RequestEye.class)) {
                    //当扫描到RequestEye注解后记录对应的接口记录
                    strings.add(method.getDeclaredAnnotation(RequestEye.class).value());
                }
            }
        }
        return strings;
    }

    /**
     * 初始化持久化数据读写服务
     *
     * @param config
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void initMapper(BlueEyeConfig config) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?> constructor = Class.forName(config.getManager()).getConstructor(AlertRecordMapper.class, InstanceMapper.class, MetricDataMapper.class, TaskMapper.class, String.class);
        JdbcMapperManager manager = (JdbcMapperManager) constructor.newInstance(Class.forName(config.getAlertMapper()).newInstance(), Class.forName(config.getInstanceMapper()).newInstance(), Class.forName(config.getMetricMapper()).newInstance(), Class.forName(config.getTaskMapper()).newInstance(), config.getProperties());
        manager.init();
        mapperManager = manager;
        AlertRecordService.alertRecordMapper = mapperManager.alertRecordMapper;
        InstanceService.instanceMapper = mapperManager.instanceMapper;
        MetricDataService.metricDataMapper = mapperManager.metricDataMapper;
        TaskService.taskMapper = mapperManager.taskMapper;
    }

    /**
     * 初始化报警服务
     *
     * @param alertConfig
     */
    public void initAlert(AlertConfig alertConfig) {
        AlertRecordService.queue = new LinkedBlockingQueue<>(alertConfig.getQueueCapacity());
        new AlertTaskConsumer().start();
        WxMsSender.token = alertConfig.getWxToken();
        WxMsSender.wxAppid = alertConfig.getWxAppid();
        WxMsSender.wxSecret = alertConfig.getWxSecret();
        WxMsSender.wxTemplateId = alertConfig.getWxTemplateId();
        NoteSender.accessKey = alertConfig.getAccessKey();
        NoteSender.accessKeySecret = alertConfig.getAccessKeySecret();
        NoteSender.signName = alertConfig.getSignName();
        NoteSender.regionId = alertConfig.getRegionId();
        NoteSender.templateCode = alertConfig.getTemplateCode();
        MailSender.mailUser = alertConfig.getMailUser();
        MailSender.mailPassword = alertConfig.getMailPassword();
    }

    /**
     * 设置接口qps监控
     *
     * @param servletContext
     * @param set
     */
    public void setInterfaceMonitor(ServletContext servletContext, Set<String> set) {
        try {
            //添加接口过滤器
            RequestFilter filter = servletContext.createFilter(RequestFilter.class);
            servletContext.addFilter("RequestFilter", filter);
        } catch (ServletException e) {
            e.printStackTrace();
        }
        FilterRegistration requestFilter = servletContext.getFilterRegistration("RequestFilter");
        System.out.println(requestFilter);
        for (String s : set) {
            //添加拦截名单
            requestFilter.addMappingForUrlPatterns(null, false, s);
        }
    }

    /**
     * 作为提供给用户进行线程池监控的入口
     *
     * @param executorName
     * @param executor
     */
    public static boolean monitorExecutor(String executorName, ThreadPoolExecutor executor, Optional<List> preTask, Optional<Long> cycle, Optional<Integer> order) {
        //判重
        if (dataCenter.getMapping().isContains(MetricsType.Executors, executorName)) {
            return false;
        }
        // 生成监控任务，定义监控逻辑
        int taskId = dataCenter.getTaskId().getAndIncrement();
        MonitorTask task = new MonitorTask((id, instanceId) -> {
            ExecutorData data = new ExecutorData();
            data.setId(dataCenter.getDataId().getAndIncrement());
            data.setTaskId(taskId);
            data.setInstanceId(instanceId);
            data.setCreateTime(new Timestamp(System.currentTimeMillis()));
            data.setCurrentUsage((double) (executor.getPoolSize() / executor.getMaximumPoolSize()));
            data.setPeakUsage((double) (executor.getLargestPoolSize() / executor.getMaximumPoolSize()));
            data.setCorePoolSize(executor.getCorePoolSize());
            data.setMaxPoolSize(executor.getMaximumPoolSize());
            data.setCurrentCount(executor.getPoolSize());
            data.setActiveCount(executor.getActiveCount());
            data.setLargestCount(executor.getLargestPoolSize());
            data.setBlockQueueSize(executor.getQueue().size() + executor.getQueue().remainingCapacity());
            data.setQueueOccupiedSize(executor.getQueue().remainingCapacity());
            data.setQueueSurplusSize(executor.getQueue().size());
            data.setCompletedTaskCount(executor.getCompletedTaskCount());
            return "线程池数据收集成功";
        });
        //分配任务id
        task.setTaskId(taskId);
        task.setPreTask(preTask.orElse(new ArrayList<Integer>()));
        task.setOrder(order.orElse(1));
        task.setCycle(cycle.orElse(0L));
        task.setCreateTime(new Timestamp(System.currentTimeMillis()));
        task.setTaskName(executorName + "监控任务");
        task.setTaskDescription("监控线程池池的情况");
        task.setIsExecuted(false);
        //存储任务
        dataCenter.getMonitorTasks().add(task);
        //存储数据源名称和任务id的映射
        dataCenter.getMapping().putMapping(MetricsType.Executors, executorName, task.getTaskId());
        //生成任务实例传入任务调度器执行
        TaskInstance instance = new TaskInstance(task.getTaskId(), dataCenter.getInstanceId().getAndIncrement(), InstanceState.READY, 1, new Timestamp(System.currentTimeMillis() + task.getCycle()), task);
        dataCenter.getInstances().addInstance(instance);
        timerScheduler.add(instance);
        return true;
    }

    /**
     * 作为提供给用户进行数据源监控的入口
     *
     * @param dataSourceName
     * @param dataSource
     */
    public static Boolean monitorDataSource(String dataSourceName, DataSource dataSource, Optional<List> preTask, Optional<Long> cycle, Optional<Integer> order) {
        //判重
        if (!dataCenter.getMapping().isContains(MetricsType.DataSource, dataSourceName)) {
            return false;
        }
        MonitorTask task = null;
        int taskId = dataCenter.getTaskId().getAndIncrement();
        // 生成监控任务，定义监控逻辑
        if (dataSource instanceof ComboPooledDataSource) {
            ComboPooledDataSource ds = (ComboPooledDataSource) dataSource;
            task = new MonitorTask((id, instanceId) -> {
                DataSourceData data = new DataSourceData();
                data.setId(dataCenter.getDataId().getAndIncrement());
                data.setTaskId(taskId);
                data.setInstanceId(instanceId);
                data.setCreateTime(new Timestamp(System.currentTimeMillis()));
                data.setDatabaseName(dataSourceName);
                data.setDatabaseType(ds.getConnection().getMetaData().getDatabaseProductName());
                data.setUsername(ds.getUser());
                data.setPassword(ds.getPassword());
                data.setUrl(ds.getJdbcUrl());
                data.setDriverClass(ds.getDriverClass());
                data.setInitialPoolSize(ds.getInitialPoolSize());
                data.setMaxPoolSize(ds.getMaxPoolSize());
                data.setMinPoolSize(ds.getMinPoolSize());
                data.setReadOnly(ds.getConnection().isReadOnly());
                data.setDefaultTransactionIsolation(ds.getConnection().getMetaData().getDefaultTransactionIsolation());
                data.setAutoCommit(ds.getConnection().getAutoCommit());
                data.setThreadsAwaiting(ds.getNumHelperThreads());
                data.setIleConnections(ds.getNumIdleConnections());
                data.setBusyConnections(ds.getNumBusyConnections());
                data.setConnections(ds.getNumConnections());
                //上传数据
                dataCenter.uploadDataSource(data);
                return "连接池数据收集成功";
            });

        } else {
            DruidDataSource ds = (DruidDataSource) dataSource;
            task = new MonitorTask((id, instanceId) -> {
                DataSourceData data = new DataSourceData();
                data.setId(dataCenter.getDataId().getAndIncrement());
                data.setTaskId(taskId);
                data.setInstanceId(instanceId);
                data.setCreateTime(new Timestamp(System.currentTimeMillis()));
                data.setDatabaseName(dataSourceName);
                data.setDatabaseType(ds.getConnection().getMetaData().getDatabaseProductName());
                data.setUsername(ds.getUsername());
                data.setPassword(ds.getPassword());
                data.setUrl(ds.getUrl());
                data.setDriverClass(ds.getDriverClassName());
                data.setInitialPoolSize(ds.getInitialSize());
                data.setMaxPoolSize(ds.getMaxActive());
                data.setMinPoolSize(ds.getMinIdle());
                data.setReadOnly(ds.getConnection().isReadOnly());
                data.setDefaultTransactionIsolation(ds.getConnection().getMetaData().getDefaultTransactionIsolation());
                data.setAutoCommit(ds.getConnection().getAutoCommit());
                data.setThreadsAwaiting(ds.getWaitThreadCount());
                data.setIleConnections((int) (ds.getConnectCount() - ds.getActiveCount()));
                data.setBusyConnections(ds.getActiveCount());
                data.setConnections((int) ds.getConnectCount());
                //上传数据
                dataCenter.uploadDataSource(data);
                return "连接池数据收集成功";
            });
        }
        //分配任务id
        task.setTaskId(taskId);
        task.setPreTask(preTask.orElse(new ArrayList<Integer>()));
        task.setOrder(order.orElse(1));
        task.setCycle(cycle.orElse(0L));
        task.setCreateTime(new Timestamp(System.currentTimeMillis()));
        task.setTaskName(dataSourceName + "监控任务");
        task.setTaskDescription("监控数据库连接池的情况");
        task.setIsExecuted(false);
        //存储任务
        dataCenter.getMonitorTasks().add(task);
        //存储数据源名称和任务id的映射
        dataCenter.getMapping().putMapping(MetricsType.DataSource, dataSourceName, task.getTaskId());
        //生成任务实例传入任务调度器执行
        TaskInstance instance = new TaskInstance(task.getTaskId(), dataCenter.getInstanceId().getAndIncrement(), InstanceState.READY, 1, new Timestamp(System.currentTimeMillis() + task.getCycle()), task);
        instance.setTask(task);
        dataCenter.getInstances().addInstance(instance);
        timerScheduler.add(instance);
        return true;

    }

    /**
     * 作为用户自定义调度任务添加的入口
     *
     * @param taskName
     * @param taskDescription
     * @param preTask
     * @param executeCallback
     */
    public static boolean addCustomizeTask(Optional<String> taskName, Optional<String> taskDescription, ExecuteCallback executeCallback, Optional<List> preTask, Optional<Long> cycle, Optional<Integer> order) {
        MonitorTask task = new MonitorTask(executeCallback);
        task.setTaskId(dataCenter.getTaskId().getAndIncrement());
        task.setTaskName(taskName.orElse("自定义调度任务") + task.getTaskId());
        task.setTaskDescription(taskDescription.orElse("自定义调度任务"));
        task.setPreTask(preTask.orElse(new ArrayList<Integer>()));
        task.setOrder(order.orElse(1));
        task.setCycle(cycle.orElse(0L));
        task.setCreateTime(new Timestamp(System.currentTimeMillis()));
        task.setIsExecuted(false);
        dataCenter.getMonitorTasks().add(task);
        TaskInstance instance = new TaskInstance(dataCenter.getInstanceId().getAndIncrement(), task.getTaskId(), InstanceState.READY, 1, new Timestamp(System.currentTimeMillis() + task.getCycle()), task);
        dataCenter.getInstances().addInstance(instance);
        timerScheduler.add(instance);
        return true;
    }

    /**
     * 作为用户元任务添加的入口
     *
     * @param taskName
     * @param taskDescription
     * @param preTask
     * @param executeCallback
     */
    public static boolean addMetaTask(Optional<String> taskName, Optional<String> taskDescription, ExecuteCallback executeCallback, Optional<List> preTask, Optional<Long> cycle, Optional<Integer> order) {
        MonitorTask task = new MonitorTask(executeCallback);
        task.setTaskId(dataCenter.getTaskId().getAndIncrement());
        task.setTaskName(taskName.orElse("元任务任务") + task.getTaskId());
        task.setTaskDescription(taskDescription.orElse("系统元任务"));
        task.setPreTask(preTask.orElse(new ArrayList<Integer>()));
        task.setOrder(order.orElse(1));
        task.setCycle(cycle.orElse(0L));
        task.setCreateTime(new Timestamp(System.currentTimeMillis()));
        task.setIsExecuted(false);
        dataCenter.getMetaTasks().add(task);
        TaskInstance instance = new TaskInstance(dataCenter.getInstanceId().getAndIncrement(), task.getTaskId(), InstanceState.READY, 1, new Timestamp(System.currentTimeMillis() + task.getCycle()), task);
        dataCenter.getInstances().addInstance(instance);
        timerScheduler.add(instance);
        return true;
    }


    /**
     * 判断前置任务是否已执行
     *
     * @return
     */
    public static boolean checkPre(int taskId) {
        //根据任务id查询前置任务id数组，遍历数组判断每个前置任务是否有已执行完成的实例
        TimerTask task = dataCenter.getTimerTaskById(taskId);
        for (Integer integer : task.getPreTask()) {
            if (!BlueEyeContext.dataCenter.getTimerTaskById(integer).getIsExecuted()) {
                return false;
            }
        }
        return true;
    }

    private static void setCommon(MetricData data, int taskId, int instanceId) {
        data.setTaskId(taskId);
        data.setId(dataCenter.getDataId().getAndIncrement());
        data.setInstanceId(instanceId);
        data.setCreateTime(new Timestamp(System.currentTimeMillis()));
    }

    private static void alertJudge(int taskId, MetricData data, String dataName) throws InterruptedException, IllegalAccessException {
        //判断数据是否需要报警
        ConcurrentHashMap<Field, AlertRule> map = dataCenter.getConfig().getMetricAlertConfig().get(taskId);
        if (map != null) {
            for (Map.Entry<Field, AlertRule> entry : map.entrySet()) {
                AlertTask alertTask = entry.getValue().getAlertTask(entry.getKey().get(data));
                if (alertTask != null) {
                    alertTask.setTaskId(dataCenter.getTaskId().getAndIncrement());
                    alertTask.setTaskName(dataName + "数据报警任务" + alertTask.getTaskId());
                    alertTask.setTaskDescription("负责" + dataName + "数据报警通知");
                    AlertRecordService.handleAlertTask(alertTask);
                }
            }

        }
    }
}
