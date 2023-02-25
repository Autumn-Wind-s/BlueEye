package SystemMonitorTest;

import com.sun.jna.Platform;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Author SDJin
 * @CreationDate 2023/2/22 21:15
 * @Description ：根据shell命令获取系统GPU状态
 */
public class GpuTest {
    @Test
    public void test() throws Exception {
        Process process = null;
        try {
            if (Platform.isWindows()) {
                process = Runtime.getRuntime().exec("nvidia-smi.exe");
            } else if (Platform.isLinux()) {
                String[] shell = {"/bin/bash", "-c", "nvidia-smi"};
                process = Runtime.getRuntime().exec(shell);
            }

//            process.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("显卡不存在或获取显卡信息失败");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        StringBuffer stringBuffer = new StringBuffer();
        ;
        String line = "";
        while ((line = reader.readLine()) != null) {
            stringBuffer.append(line + "\n");
        }
        //分割字符串可获取GPU温度和使用率
        System.out.println(stringBuffer.toString());

    }
}
