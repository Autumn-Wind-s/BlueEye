import blueeye.compression.MetricDataCompressor;
import blueeye.decompression.MetricDataDecompressor;
import blueeye.pojo.instance.InstanceState;
import blueeye.pojo.metric.MetricData;
import blueeye.pojo.metric.system.CpuData;
import blueeye.pojo.po.MetricDataPo;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;

/**
 * @Author SDJin
 * @CreationDate 2023/7/23 23:31
 * @Description ：
 */
public class MetricDataCompressTest {
    @Test
    public void test() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        CpuData cpuData = new CpuData();
        cpuData.setTemperature(11.0);
        cpuData.setUsageRate(78.0);
        cpuData.setId(1);
        cpuData.setTaskId(2);
        cpuData.setInstanceId(3);
        cpuData.setCreateTime(new Timestamp(1111));
        MetricDataPo compress = MetricDataCompressor.compress(cpuData);
        System.out.println(compress.getJson());
        CpuData decompress = (CpuData) MetricDataDecompressor.decompress(compress);
        System.out.println(decompress.getUsageRate());
    }
    @Test
    public void test1(){
        String a= String.valueOf(InstanceState.RUNNING);
        InstanceState instanceState = InstanceState.valueOf(a);
        System.out.println(instanceState);
        System.out.println(a);
    }
}
