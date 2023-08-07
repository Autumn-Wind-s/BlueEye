package blueeye.util;

import blueeye.pojo.metric.system.NoHeapData;
import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

/**
 * @Author SDJin
 * @CreationDate 2023/2/24 16:41
 * @Description ：使用MemoryMXBean获取非堆内存初始字节数、最大字节数、已使用字节数、可用字节数
 */
public class NoHeapUtil {

    public static void gatherData(NoHeapData data) {
        MemoryMXBean mxb = ManagementFactory.getMemoryMXBean();
        data.setMax(mxb.getNonHeapMemoryUsage().getMax() / 1024);
        data.setInit(mxb.getNonHeapMemoryUsage().getInit() / 1024);
        data.setUsage(mxb.getNonHeapMemoryUsage().getUsed() / 1024);
        data.setUsable(mxb.getNonHeapMemoryUsage().getCommitted() / 1024 - data.getUsage());
    }
}
