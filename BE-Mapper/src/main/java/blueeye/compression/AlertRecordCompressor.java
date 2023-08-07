package blueeye.compression;

import blueeye.pojo.alert.AlertRecord;
import blueeye.pojo.po.AlertRecordPo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author SDJin
 * @CreationDate 2023/6/19 20:47
 * @Description ：
 */
public class AlertRecordCompressor {
    public static AlertRecordPo compress(AlertRecord record){
        return new AlertRecordPo(record.getRecordId(),record.getTaskId(),record.getAlertTime(),record.getContent(),record.getNotifier(),String.valueOf(record.getAlertMethod()));
    }
    public static List<AlertRecordPo> compress(List<AlertRecord> records){
        List<AlertRecordPo> alertRecordPos = new ArrayList<>();
        for (AlertRecord record : records) {
            alertRecordPos.add(compress(record));
        }
        return alertRecordPos;
    }
}
