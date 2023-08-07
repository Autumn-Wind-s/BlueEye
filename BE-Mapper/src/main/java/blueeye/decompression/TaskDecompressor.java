package blueeye.decompression;

import blueeye.pojo.po.TaskPo;
import blueeye.pojo.task.impl.TimerTask;
import blueeye.pojo.task.impl.intf.InterfaceTask;
import blueeye.pojo.task.impl.monitor.MonitorTask;
import blueeye.pojo.task.impl.script.ScriptTask;
import blueeye.rdb.KryoUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author SDJin
 * @CreationDate 2023/6/19 20:03
 * @Description ：
 */
public class TaskDecompressor {


    /**
     * 类型编号与对应反压缩方法名的映射
     */
    private final static HashMap<Integer,String> MAPPING = new HashMap<>();
    static {
        MAPPING.put(0,"decompressMonitorTask");
        MAPPING.put(1,"decompressInterfaceTask");
        MAPPING.put(2,"decompressScriptTask");
    }

    /**
     * 作为反压缩的入口
     * @param taskpo taskpo对象
     * @return 反压缩获得的任务对象
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static TimerTask decompress(TaskPo taskpo) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String methodName=MAPPING.get(taskpo.getType());
        return (TimerTask) TaskDecompressor.class.getDeclaredMethod(methodName, taskpo.getClass()).invoke(null,taskpo);
    }

    /**
     * 作为反压缩的入口
     * @param taskPos taskpo对象
     * @return 反压缩获得的任务对象
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static List<? extends TimerTask> decompress(List<TaskPo> taskPos) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if(taskPos.size()==0){
            return null;
        }
        String methodName=MAPPING.get(taskPos.get(0).getType());
        return (List<? extends TimerTask>) TaskDecompressor.class.getDeclaredMethod(methodName, List.class).invoke(null,taskPos);
    }
    /**
     * 将taskPo解压为监控任务对象
     *
     * @param taskPo taskPo对象
     * @return 解压所得的监控任务对象
     */
    private static MonitorTask decompressMonitorTask(TaskPo taskPo) {
        MonitorTask task = new MonitorTask(KryoUtil.readFromString(taskPo.getJson()));
        setCommon(task,taskPo);
        return task;
    }

    private static List<MonitorTask> decompressMonitorTask(List<TaskPo> taskPos) {
        List<MonitorTask> list = new ArrayList<>();
        for (TaskPo taskPo : taskPos) {
            list.add(decompressMonitorTask(taskPo));
        }
        return list;
    }

    /**
     * 将taskPo解压为接口任务对象
     * @param taskPo taskPo对象
     * @return 接口任务对象
     */
    private static InterfaceTask decompressInterfaceTask(TaskPo taskPo) {
        InterfaceTask task = new InterfaceTask();
        setCommon(task,taskPo);
        String[] split = taskPo.getJson().split("\n");
        task.setUrl(split[0]);
        task.setRequestMode(split[1]);
        task.setParams(KryoUtil.readFromString(split[2]));
        task.setRequestHeaders(KryoUtil.readFromString(split[3]));
        return task;
    }

    private static List<InterfaceTask> decompressInterfaceTask(List<TaskPo> taskPos){
        List<InterfaceTask> list=new ArrayList<>();
        for (TaskPo taskPo : taskPos) {
            list.add(decompressInterfaceTask(taskPo));
        }
        return list;
    }

    /**
     * 将taskPo解压为脚本任务对象
     * @param taskPo taskPo对象
     * @return 脚本任务对象
     */
    private static ScriptTask decompressScriptTask(TaskPo taskPo){
        ScriptTask task = new ScriptTask();
        setCommon(task,taskPo);
        task.setShell(taskPo.getJson());
        return task;
    }
    private static List<ScriptTask> decompressScriptTask(List<TaskPo> taskPos){
        List<ScriptTask> list=new ArrayList<>();
        for (TaskPo taskPo : taskPos) {
            list.add(decompressScriptTask(taskPo));
        }
        return list;
    }

    /**
     * 设置公共属性
     * @param task
     * @param taskPo
     */
    private static void setCommon(TimerTask task,TaskPo taskPo){
        task.setTaskId(taskPo.getTaskId());
        task.setTaskName(taskPo.getTaskName());
        task.setTaskDescription(taskPo.getTaskDescription());
        task.setCreateTime(taskPo.getCreateTime());
        task.setCycle(taskPo.getCycle());
        String preTask = taskPo.getPreTask();
        if(preTask!=null&&preTask.length()!=0){
            String[] split = taskPo.getPreTask().split(",");
            System.out.println(split.length);
            task.setPreTask(Stream.of(split).map(Integer::valueOf).collect(Collectors.toList()));
        }
        task.setIsExecuted(Boolean.getBoolean(taskPo.getIsExecuted()));
        task.setOrder(taskPo.getOrder());
    }
}
