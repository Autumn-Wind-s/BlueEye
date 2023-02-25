package SystemMonitorTest;

import cn.hutool.system.oshi.OshiUtil;
import org.junit.Test;

import java.text.DecimalFormat;

/**
 * @Author SDJin
 * @CreationDate 2023/2/22 21:29
 * @Description ：使用oshi获取系统内存使用量、总量、使用率
 */
public class MemoryTest {
    @Test
    public void test() {
        getMemoryInfo();
    }

    /**
     * 获取内存数据
     */
    public static void getMemoryInfo() {
        long total = OshiUtil.getMemory().getTotal() / 1024 / 1024;
        long use = total - OshiUtil.getMemory().getAvailable() / 1024 / 1024;
        DecimalFormat format = new DecimalFormat("#.00");
        double usageRate = Double.parseDouble(format.format((double) use / (double) total));
        System.out.println("内存总量：" + total + "Mb");
        System.out.println("已用内存：" + use + "Mb");
        System.out.println("内存使用率：" + usageRate*100+"%");
    }
}
