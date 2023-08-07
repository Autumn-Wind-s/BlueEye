package blueeye.util;

import blueeye.pojo.metric.system.GcData;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Author SDJin
 * @CreationDate 2023/2/24 18:04
 * @Description ：根据Jstat获取gc信息
 */
public class GcUtil {
    public static void gatherData(GcData data) throws IOException {
        StringBuffer gc = new StringBuffer();
        //通过jps命令获取jvm的进程id
        int id = JvmUtil.getVmid();
        //使用jstat -gccause
        Process exec = Runtime.getRuntime().exec("jstat -gccause " + id);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
        String s;
        while ((s = bufferedReader.readLine()) != null) {
            gc.append(s + "\n");
        }
        //分割gc数据
        String[] split = gc.toString().split("\n")[1].split("\\s+");
        data.setYongGcFrequency(Long.parseLong(split[7]));
        data.setYongGcTime(Double.parseDouble(split[8]));
        data.setFullGcFrequency(Long.parseLong(split[9]));
        data.setFullGcTime(Double.parseDouble(split[10]));
    }
}
