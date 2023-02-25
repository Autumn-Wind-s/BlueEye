package SystemMonitorTest;

import com.util.JvmUtil;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Author SDJin
 * @CreationDate 2023/2/24 18:04
 * @Description ：根据Jstat获取gc信息
 */
public class GcTest {
    @Test
    public void test() throws IOException {
        StringBuffer gc=new StringBuffer();
        //通过jps命令获取jvm的进程id
        int id=JvmUtil.getVmid();
        //使用jstat -gccause
        Process exec = Runtime.getRuntime().exec("jstat -gccause " + id);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
        String s;
        while((s=bufferedReader.readLine())!=null){
            gc.append(s+"\n");
        }
        //todo 分割gc数据
        System.out.println(gc.toString());
    }

}
