package blueeye.util;

import blueeye.pojo.metric.system.JvmThreadData;
import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * @Author SDJin
 * @CreationDate 2023/2/24 19:08
 * @Description ：通过ThreadMXBean获取线程总数量、死锁线程数量、阻塞线程数量、可运行线程数量、限时等待线程数量、等待中线程数量
 */
public class JvmThreadUtil {

    public static void gatherData(JvmThreadData data){
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        //返回所有活动线程 ID。
        long[] allThreadIds = threadMXBean.getAllThreadIds();
        int runnable=0,blocked=0,waiting=0,timeWaiting=0;
        //获取所有线程的信息
        ThreadInfo[] threadInfo = threadMXBean.getThreadInfo(allThreadIds);
        for (ThreadInfo info : threadInfo) {
            Thread.State state = info.getThreadState();
            if (state.equals(Thread.State.RUNNABLE)){
                runnable++;
            }else if(state.equals(Thread.State.BLOCKED)){
                blocked++;
            }else if(state.equals(Thread.State.WAITING)){
                waiting++;
            }else if(state.equals(Thread.State.TIMED_WAITING)){
                timeWaiting++;
            }
        }
        data.setTotal(allThreadIds.length);
        data.setRunnable(runnable);
        data.setBlocked(blocked);
        data.setWaiting(waiting);
        data.setTimeWaiting(timeWaiting);
    }
}
