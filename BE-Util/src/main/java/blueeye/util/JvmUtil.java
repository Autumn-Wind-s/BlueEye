package blueeye.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Author SDJin
 * @CreationDate 2023/2/24 18:07
 * @Description ：
 */
public class JvmUtil {
    static int vmid;

    static {
        //获取jvm的进程id
        try {
            Process jps = Runtime.getRuntime().exec("jps");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(jps.getInputStream()));
            String s = "";
            while ((s = bufferedReader.readLine()) != null) {
                if (s.indexOf("Launcher") != -1) {
                    break;
                }
            }
            vmid = Integer.parseInt(s.substring(0, s.indexOf(" ")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getVmid() {
        return vmid;
    }
}
