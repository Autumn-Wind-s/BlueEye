package SystemMonitorTest;

import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;
import org.junit.Test;
import oshi.hardware.Sensors;
import oshi.util.GlobalConfig;

import java.io.File;
import java.text.DecimalFormat;

/**
 * @Author SDJin
 * @CreationDate 2023/2/22 18:07
 * @Description ：根据oshi获取系统cpu信息
 */
public class CpuTest {
    @Test
    public void test() throws InterruptedException {

        GlobalConfig.set(GlobalConfig.OSHI_OS_WINDOWS_CPU_UTILITY, true);
        //获取cpu利用率
        getOsInfo();
        //获取Cpu温度,windows下失效，Windows电脑拿不到CPU传感器
        getOsCpuTem();


    }

    /**
     * 获取cpu利用率
     */
    public static void getOsInfo() {
        CpuInfo cpuInfo = OshiUtil.getCpuInfo();

        double free = cpuInfo.getFree();
        DecimalFormat format = new DecimalFormat("#.00");
        System.out.println("cpu利用率：" + Double.parseDouble(format.format(100.0D - free))+"%");
    }

    public static void getOsCpuTem() {
        //传感器（温度，风扇速度，电压）
        Sensors sensors = OshiUtil.getSensors();
        //处理器温度
        System.out.println("温度:" + sensors.getCpuTemperature());
    }




}
