package blueeye.util;

import blueeye.pojo.metric.system.LoadData;
import com.sun.jna.Platform;
import com.sun.management.OperatingSystemMXBean;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;

/**
 * @Author SDJin
 * @CreationDate 2023/2/23 20:17
 * @Description ：根据shell命令获取系统Load
 */
public class LoadUtil {
    public static void gatherData(LoadData data) throws IOException {
        if (Platform.isWindows()){
            //windows系统无法查看load
            data.setOneMinute(0.0);
            data.setFifteenMinute(0.0);
            data.setFifteenMinute(0.0);
        }else {
            OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
            String[] shell = {"/bin/bash", "-c", "uptime"};
            Process process = Runtime.getRuntime().exec(shell);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String s = bufferedReader.readLine();
            String a = "load average:";
            int index = s.indexOf(a);
            String[] split = s.substring(index + a.length(), s.length()).split(",");
            data.setOneMinute(Double.parseDouble(split[0]));
            data.setFifteenMinute(Double.parseDouble(split[1]));
            data.setFifteenMinute(Double.parseDouble(split[2]));
        }

    }

}
