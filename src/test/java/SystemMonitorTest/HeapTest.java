package SystemMonitorTest;

import com.util.JvmUtil;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

/**
 * @Author SDJin
 * @CreationDate 2023/2/24 16:27
 * @Description ：MemoryMXBean+jstat获取jvm堆内存总和、堆内存使用量、堆内存老年代字节数、堆内存年轻代Survivor区字节数、堆内存年轻代Eden区字节数、堆内存最大字节数
 */
public class HeapTest {
    @Test
    public void  test() throws IOException {
        //堆的宏观内存可以通过MemoryMXBean获取
        MemoryMXBean mxb = ManagementFactory.getMemoryMXBean();
        //java虚拟机设置的可以用于内存管理的最大内存量。
        System.out.println("Max:" +mxb.getHeapMemoryUsage().getMax());    //Max:1776MB
        //java虚拟机在启动的时候向操作系统请求的初始内存容量。
        System.out.println("Init:" + mxb.getHeapMemoryUsage().getInit());  //Init:126MB
        //当前可使用堆内存总和(包括已使用)，动态变化
        System.out.println("Committed:" + mxb.getHeapMemoryUsage().getCommitted());   //Committed:121MB
        //当前已使用的堆内存，
        System.out.println("Used:" + mxb.getHeapMemoryUsage().getUsed());  //Used:7MB
        //内存使用总描述
        System.out.println(mxb.getHeapMemoryUsage().toString());

        //堆的survivor、eden和老年代的信息需用jstat -gccapacity获取
        StringBuffer generation=new StringBuffer();
        //获取jvm的id
        int id= JvmUtil.getVmid();
        //执行jstat -gccapacity命令获取信息，单位kb
        Process exec = Runtime.getRuntime().exec("jstat -gccapacity " + id);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
        String s;
        while((s=bufferedReader.readLine())!=null){
            generation.append(s+"\n");
        }
        //todo 分割generation数据
        System.out.println(generation.toString());


    }
}
