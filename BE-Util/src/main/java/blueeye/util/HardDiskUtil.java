package blueeye.util;

import blueeye.pojo.metric.system.HardDiskData;
import org.junit.Test;

import java.io.File;

/**
 * @Author SDJin
 * @CreationDate 2023/2/22 21:45
 * @Description ：直接操作File获取磁盘使用情况
 */
public class HardDiskUtil {

    /**
     * 获取硬盘使用量
     */
    public static void gatherData(HardDiskData data) {
        File[] files = File.listRoots();
        long total = 0, freeSpace = 0;
        for (File file : files) {
            total += file.getTotalSpace();
            freeSpace += file.getFreeSpace();
        }
        data.setTotal(total / 1024 / 1024 / 1024);
        data.setUsage((total - freeSpace) / 1024 / 1024 / 1024);
        data.setUsageRate((double) (data.getUsage() * 100 / data.getTotal()));
    }
}
