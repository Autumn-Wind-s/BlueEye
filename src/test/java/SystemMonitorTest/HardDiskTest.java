package SystemMonitorTest;

import com.sun.management.OperatingSystemMXBean;
import org.junit.Test;

import javax.management.MXBean;
import java.io.File;
import java.lang.management.ManagementFactory;

/**
 * @Author SDJin
 * @CreationDate 2023/2/22 21:45
 * @Description ：直接操作File获取磁盘使用情况
 */
public class HardDiskTest {
    @Test
    public void test() {
        getDiskUsed();
    }
    /**
     * 获取硬盘使用量
     */
    public static void getDiskUsed() {
        File[] files = File.listRoots();
        for (File file: files) {
            long total = file.getTotalSpace();
            long freeSpace = file.getFreeSpace();
            System.out.println("磁盘总量：" + total / 1024 / 1024 / 1024);
            System.out.println("磁盘剩余总量：" + freeSpace / 1024 / 1024 / 1024);
            System.out.println("磁盘已用总量：" + (total - freeSpace) / 1024 / 1024 / 1024);
        }
    }
}
