import blueeye.compression.TaskCompressor;
import blueeye.decompression.TaskDecompressor;
import blueeye.pojo.po.TaskPo;
import blueeye.pojo.task.impl.TimerTask;
import blueeye.pojo.task.impl.script.ScriptTask;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author SDJin
 * @CreationDate 2023/7/22 21:29
 * @Description ：
 */
public class test1 {
    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        ScriptTask task = new ScriptTask();
        task.setTaskId(1);
        task.setTaskName("script");
        task.setShell("sdad");
        task.setTaskDescription("第一个");
        task.setOrder(1);
        List<Integer> list = new ArrayList<>();
        task.setPreTask(new ArrayList<Integer>());
        task.setCreateTime(new Timestamp(123));
        task.setCycle(123L);
//        TaskPo compress = TaskCompressor.compress(task);
        List<ScriptTask> scriptTasks = new ArrayList<>();
        scriptTasks.add(task);
        List<TaskPo> compress = TaskCompressor.compress(scriptTasks);
        System.out.println(compress.get(0).getJson());
        List<? extends TimerTask> decompress = TaskDecompressor.decompress(compress);
        System.out.println(decompress.get(0).getClass());
    }
}
