package blueeye.compression;

import blueeye.pojo.po.TaskPo;
import blueeye.pojo.task.impl.TimerTask;
import blueeye.pojo.task.impl.intf.InterfaceTask;
import blueeye.pojo.task.impl.monitor.MonitorTask;
import blueeye.pojo.task.impl.script.ScriptTask;
import blueeye.util.KryoUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author SDJin
 * @CreationDate 2023/6/18 16:09
 * @Description ：
 */
public class TaskCompressor {
    /**
     * 任务类型与压缩方法名的映射
     */
    private final static HashMap<Class, String> MAPPING = new HashMap<>();

    static {
        MAPPING.put(MonitorTask.class, "compressMonitorTask");
        MAPPING.put(InterfaceTask.class, "compressInterfaceTask");
        MAPPING.put(ScriptTask.class, "compressScriptTask");
    }

    /**
     * 作为压缩的入口
     *
     * @param task 待压缩的任务对象
     * @return 压缩后的TaskPo对象
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static TaskPo compress(TimerTask task) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<? extends TimerTask> aClass = task.getClass();
        String methodName = MAPPING.get(aClass);
        Object invoke = TaskCompressor.class.getDeclaredMethod(methodName, aClass).invoke(null, task);
        return (TaskPo) invoke;
    }

    /**
     * 作为压缩的入口
     *
     * @param tasks 待压缩的任务对象
     * @return 压缩后的TaskPo对象
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static List<TaskPo> compress(List<? extends TimerTask> tasks) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (tasks.size() == 0) {
            return null;
        }
        Class<? extends TimerTask> aClass = tasks.get(0).getClass();
        String methodName = MAPPING.get(aClass);
        Object invoke = TaskCompressor.class.getDeclaredMethod(methodName, List.class).invoke(null, tasks);
        return (List<TaskPo>) invoke;
    }


    /**
     * 将监控任务压缩为TaskPo
     *
     * @param task 监控任务
     * @return taskPo对象
     */
    private static TaskPo compressMonitorTask(MonitorTask task) {
        TaskPo taskPo = new TaskPo(task.getTaskId(), task.getTaskName(), task.getTaskDescription(), task.getCreateTime(), 0, task.getCycle(), task.getPreTask() == null ? null : task.getPreTask().stream().map(String::valueOf).collect(Collectors.joining(",")), task.getIsExecuted().toString(), task.getOrder());
        //将任务执行体转化为String存入json中
        String s = KryoUtil.writeToString(task.getMonitorCallback());
        taskPo.setJson(s);
        return taskPo;
    }

    private static List<TaskPo> compressMonitorTask(List<MonitorTask> tasks) {
        List<TaskPo> taskPos = new ArrayList<>();
        for (MonitorTask task : tasks) {
            taskPos.add(compressMonitorTask(task));
        }
        return taskPos;
    }

    /**
     * 将接口任务压缩为TaskPo
     *
     * @param task 接口任务对象
     * @return taskPo对象
     */
    private static TaskPo compressInterfaceTask(InterfaceTask task) {
        TaskPo taskPo = new TaskPo(task.getTaskId(), task.getTaskName(), task.getTaskDescription(), task.getCreateTime(), 0, task.getCycle(), task.getPreTask() == null ? null : task.getPreTask().stream().map(String::valueOf).collect(Collectors.joining(",")), task.getIsExecuted().toString(), task.getOrder());
        //将接口任务的特殊属性封装到json属性
        String json = task.getUrl() + "\n" + task.getRequestMode() + "\n" + KryoUtil.writeToString(task.getParams()) + "\n" + KryoUtil.writeToString(task.getRequestHeaders());
        taskPo.setJson(json);
        return taskPo;
    }

    private static List<TaskPo> compressInterfaceTask(List<InterfaceTask> tasks) {
        List<TaskPo> list = new ArrayList<>();
        for (InterfaceTask task : tasks) {
            list.add(compressInterfaceTask(task));
        }
        return list;
    }

    /**
     * 将脚本任务压缩为TaskPo
     *
     * @param task 脚本任务对象
     * @return taskPo对象
     */
    private static TaskPo compressScriptTask(ScriptTask task) {
        TaskPo taskPo = new TaskPo(task.getTaskId(), task.getTaskName(), task.getTaskDescription(), task.getCreateTime(), 0, task.getCycle(), task.getPreTask() == null ? null : task.getPreTask().stream().map(String::valueOf).collect(Collectors.joining(",")), task.getIsExecuted().toString(), task.getOrder());
        //将脚本任务的特殊属性封装到json属性中
        taskPo.setJson(task.getShell());
        return taskPo;
    }

    private static List<TaskPo> compressScriptTask(List<ScriptTask> tasks) {
        List<TaskPo> list = new ArrayList<>();
        for (ScriptTask task : tasks) {
            list.add(compressScriptTask(task));
        }
        return list;
    }
}
