package blueeye.decompression;

import blueeye.pojo.alert.AlertMethod;
import blueeye.pojo.alert.AlertRecord;
import blueeye.pojo.po.AlertRecordPo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author SDJin
 * @CreationDate 2023/6/19 20:47
 * @Description ：
 */
public class AlertRecordDecompressor {
    public static AlertRecord decompress(AlertRecordPo recordPo) {
        return new AlertRecord(recordPo.getRecordId(), recordPo.getTaskId(), recordPo.getAlertTime(), recordPo.getContent(), recordPo.getNotifier(), AlertMethod.valueOf(recordPo.getAlertMethod()));
    }
    public static List<AlertRecord> decompress(List<AlertRecordPo> recordPos){
        List<AlertRecord> alertRecords = new ArrayList<>();
        for (AlertRecordPo recordPo : recordPos) {
            alertRecords.add(decompress(recordPo));
        }
        return alertRecords;
    }
}
