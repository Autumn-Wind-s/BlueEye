package SystemMonitorTest;

import org.junit.Test;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * @Author SDJin
 * @CreationDate 2023/2/24 19:08
 * @Description ：通过ThreadMXBean获取线程总数量、死锁线程数量、阻塞线程数量、可运行线程数量、限时等待线程数量、等待中线程数量
 */
public class JvmThreadTest {
    @Test
    public void test(){
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        //返回所有活动线程 ID。
        long[] allThreadIds = threadMXBean.getAllThreadIds();
        //获取所有线程的信息
        ThreadInfo[] threadInfo = threadMXBean.getThreadInfo(allThreadIds);
        // todo 根据线程信息进行统计
    }
}
