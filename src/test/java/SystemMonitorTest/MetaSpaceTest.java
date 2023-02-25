package SystemMonitorTest;

import com.util.JvmUtil;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Author SDJin
 * @CreationDate 2023/2/24 18:24
 * @Description ：根据jstat获取元空间字节数,元空间最大的容量,元空间最小的容量
 */
public class MetaSpaceTest {
    @Test
    public void test() throws IOException {
        StringBuffer meteSpace=new StringBuffer();
        //获取jvm的id
        int id= JvmUtil.getVmid();
        //执行jstat -gcmetacapacity命令获取元空间数据
        Process exec = Runtime.getRuntime().exec("jstat -gcmetacapacity " + id);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
        String s;
        while((s=bufferedReader.readLine())!=null){
            meteSpace.append(s+"\n");
        }
        //todo 分割meteSpace数据
        System.out.println(meteSpace.toString());
    }
}
