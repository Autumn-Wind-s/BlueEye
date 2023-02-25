package SystemMonitorTest;

import com.sun.management.OperatingSystemMXBean;
import org.junit.Test;

import javax.sound.midi.Soundbank;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;

/**
 * @Author SDJin
 * @CreationDate 2023/2/23 20:17
 * @Description ：根据shell命令获取系统Load
 */
public class LoadTest {
    @Test
    public void test() throws IOException {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
// What % CPU load this current JVM is taking, from 0.0-1.0
        System.out.println(osBean.getProcessCpuLoad());//指CPU的负载情况
// What % load the overall system is at, from 0.0-1.0
        System.out.println(osBean.getSystemCpuLoad());//指系统的负载情况
        //
        String[] shell = {"/bin/bash", "-c", "uptime"};
        Process process=Runtime.getRuntime().exec(shell);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String s=bufferedReader.readLine();
        //todo 分割字符串获取结果
    }

}
