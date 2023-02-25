package SystemMonitorTest;

import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

/**
 * @Author SDJin
 * @CreationDate 2023/2/24 16:41
 * @Description ：使用MemoryMXBean获取非堆内存初始字节数、最大字节数、已使用字节数、可用字节数
 */
public class NoHeapTest {
    @Test
    public void test(){
        MemoryMXBean mxb = ManagementFactory.getMemoryMXBean();
        
        System.out.println("Max:" +mxb.getNonHeapMemoryUsage().getMax());    //Max:0MB
        System.out.println("Init:" + mxb.getNonHeapMemoryUsage().getInit());  //Init:2MB
        System.out.println("Committed:" + mxb.getNonHeapMemoryUsage().getCommitted());   //Committed:8MB
        System.out.println("Used:" + mxb.getNonHeapMemoryUsage().getUsed());  //Used:7MB
        System.out.println(mxb.getNonHeapMemoryUsage().toString());
    }
}
