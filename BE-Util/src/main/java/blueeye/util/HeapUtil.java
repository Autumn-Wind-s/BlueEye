package blueeye.util;

import blueeye.pojo.metric.system.HeapData;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

/**
 * @Author SDJin
 * @CreationDate 2023/2/24 16:27
 * @Description ：MemoryMXBean+jstat获取jvm堆内存总和、堆内存使用量、堆内存老年代字节数、堆内存年轻代Survivor区字节数、堆内存年轻代Eden区字节数、堆内存最大字节数
 */
public class HeapUtil {


    public static void gatherData(HeapData data) throws IOException {
        //堆的宏观内存可以通过MemoryMXBean获取
        MemoryMXBean mxb = ManagementFactory.getMemoryMXBean();
        data.setMax(mxb.getHeapMemoryUsage().getMax() / 1024);
        data.setTotal(mxb.getHeapMemoryUsage().getCommitted() / 1024);
        data.setUsage(mxb.getHeapMemoryUsage().getUsed() / 1024);
        //堆的survivor、eden和老年代的信息需用jstat -gccapacity获取
        StringBuffer generation = new StringBuffer();
        //获取jvm的id
        int id = JvmUtil.getVmid();
        //执行jstat -gccapacity命令获取信息，单位kb
        Process exec = Runtime.getRuntime().exec("jstat -gccapacity " + id);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
        String s;
        while ((s = bufferedReader.readLine()) != null) {
            generation.append(s + "\n");
        }
        // 分割generation数据
        String[] split = generation.toString().split("\n")[1].split("\\s+");
        data.setSurvivor0((long) Double.parseDouble(split[4]));
        data.setSurvivor1((long) Double.parseDouble(split[5]));
        data.setEden((long) Double.parseDouble(split[6]));
        data.setOld((long) Double.parseDouble(split[10]));
        data.setMetSpace((long) Double.parseDouble(split[13]));
    }


}
