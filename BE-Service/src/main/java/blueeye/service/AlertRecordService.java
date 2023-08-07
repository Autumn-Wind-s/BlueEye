package blueeye.service;

import blueeye.persistent.AlertRecordMapper;
import blueeye.pojo.task.impl.alert.AlertTask;

import java.util.concurrent.BlockingQueue;

/**
 * @Author SDJin
 * @CreationDate 2023/6/2 15:46
 * @Description ：
 */
public class AlertRecordService {
    public static AlertRecordMapper alertRecordMapper;
    public static BlockingQueue<AlertTask> queue ;
    public static void handleAlertTask(AlertTask task) throws InterruptedException {
        queue.put(task);
    }

}
