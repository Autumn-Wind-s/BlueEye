package blueeye.util;

import blueeye.pojo.metric.system.MemoryData;
import cn.hutool.system.oshi.OshiUtil;
import org.junit.Test;

import java.text.DecimalFormat;

/**
 * @Author SDJin
 * @CreationDate 2023/2/22 21:29
 * @Description ：使用oshi获取系统内存使用量、总量、使用率
 */
public class MemoryUtil {

    /**
     * 获取内存数据
     */
    public static void gatherData(MemoryData data) {
        long total = OshiUtil.getMemory().getTotal() / 1024 / 1024;
        long use = total - OshiUtil.getMemory().getAvailable() / 1024 / 1024;
        DecimalFormat format = new DecimalFormat("#.00");
        double usageRate = Double.parseDouble(format.format((double) use / (double) total));
        data.setTotal(total);
        data.setUsage(use);
        data.setUsageRate(usageRate*100);
     }
}
