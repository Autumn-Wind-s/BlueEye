import blueeye.center.DataCenter;
import blueeye.container.*;
import blueeye.context.BlueEyeContext;
import blueeye.mapping.MetricTaskIdMapping;
import blueeye.pojo.instance.TaskInstance;
import blueeye.pojo.metric.system.HeapData;
import blueeye.pojo.task.impl.monitor.ExecuteCallback;
import blueeye.pojo.task.impl.monitor.MonitorTask;
import blueeye.rdb.Rdb;
import blueeye.rdb.Snapshot;
import blueeye.util.CpuUtil;
import blueeye.util.JvmUtil;
import blueeye.util.KryoUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author SDJin
 * @CreationDate 2023/8/4 23:59
 * @Description ：
 */
@Slf4j
public class Test {
    public static void main(String[] args) throws DocumentException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, InterruptedException {
        log.info("开始");
        BlueEyeContext blueEyeContext = new BlueEyeContext();
        blueEyeContext.init(null);
////        Thread.sleep(5000);
//        System.out.println(BlueEyeContext.dataCenter);
//        Thread.sleep(2000);
//        BlueEyeContext. rdb.backup();
////            System.out.println("备份完毕");
//        Rdb rdb = new Rdb(new DataCenter(), Optional.of("null"), Optional.of(2));
//        DataCenter restore = rdb.restore("E:\\IDEA2\\BlueEye\\RDB1.txt");
//        for (TaskInstance instance : restore.getInstances().getActiveInstance()) {
//            System.out.println(instance.getTask().getTaskName()+":"+instance);
//        }
    }

    @org.junit.Test
    public void test() throws IOException, DocumentException, ClassNotFoundException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Rdb rdb = new Rdb(new DataCenter(), Optional.of("null"), Optional.of(1));
        DataCenter restore = rdb.restore("E:\\IDEA2\\BlueEye\\rdbRDB0.txt");
        System.out.println(restore);
    }

    @org.junit.Test
    public void test2() throws IOException {
        DataCenter data = new DataCenter.DataCenterBuilder().metaTasks(new TaskContainer()).monitorTasks(new TaskContainer()).interfaceTasks(new TaskContainer()).scriptTasks(new TaskContainer()).metrics(new MetricDataContainer()).instances(new InstanceContainer()).records(new AlertRecordContainer()).mapping(new MetricTaskIdMapping()).taskId(new AtomicInteger()).instanceId(new AtomicInteger()).dataId(new AtomicInteger()).recordId(new AtomicInteger()).config(new ConfigContainer()).build();
//        data.setDataId(new AtomicInteger());
//        data.getDataId().getAndIncrement();

        Rdb rdb = new Rdb(data, Optional.ofNullable("E:\\IDEA2\\BlueEye\\"), Optional.ofNullable(1));
        MonitorTask task = new MonitorTask(new ExecuteCallback() {
            @Override
            public String execute(int taskId, int instanceId) throws Exception {
                return "sdada";
            }
        });
        task.setTaskId(1);
        task.setTaskName("ni");
        data.getMonitorTasks().add(task);
        rdb.backup();

//        Rdb rdb = new Rdb("E:\\IDEA2\\BlueEye\\RDB0.txt");
        DataCenter restore = rdb.restore("E:\\IDEA2\\BlueEye\\RDB0.txt");

        System.out.println(restore.getDataId().get());
        //        System.out.println(restore);
//        byte[] bytes = KryoUtil.writeObjectToByteArray(data);
//        System.out.println(data);
//        FileOutputStream outputStream = new FileOutputStream("E:\\IDEA2\\BlueEye\\rdb.txt");
//        Snapshot snapshot = data.createSnapshot();
//        byte[] bytes = KryoUtil.writeObjectToByteArray(snapshot);
//        outputStream.write(bytes);
//        outputStream.close();
//        FileInputStream fileInputStream = new FileInputStream("E:\\IDEA2\\BlueEye\\rdbRDB0.txt");
//        int available = fileInputStream.available();
//        byte[] bytes1 = new byte[available];
//        fileInputStream.read(bytes1);
////        Snapshot snapshot = KryoUtil.readObjectFromByteArray(bytes1, Snapshot.class);
//
//        Snapshot s = (Snapshot) KryoUtil.readObjectFromByteArray(bytes1, get(DataCenter.class,"DataSnapshot"));
//        DataCenter dataCenter = new DataCenter();
//        dataCenter.restoreSnapshot(s);
//        System.out.println(dataCenter.getDataId().get());

    }

    @org.junit.Test
    public void test5() {


    }

    public Class get(Class c, String className) {
        Class[] declaredClasses = c.getDeclaredClasses();
        for (Class declaredClass : declaredClasses) {
            if (declaredClass.getName().equals(c.getName() + "$" + className)) {
                return declaredClass;
            }
        }
        return null;
    }

}
