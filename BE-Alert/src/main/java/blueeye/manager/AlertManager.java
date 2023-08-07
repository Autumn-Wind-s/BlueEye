package blueeye.manager;

import blueeye.center.DataCenter;
import blueeye.config.AlertConfig;
import blueeye.consumer.AlertTaskConsumer;
import blueeye.email.MailSender;
import blueeye.note.NoteSender;
import blueeye.pojo.alert.AlertRule;
import blueeye.pojo.metric.MetricData;
import blueeye.pojo.task.impl.alert.AlertTask;
import blueeye.wx.WxMsSender;
import com.sun.org.apache.bcel.internal.generic.DADD;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author SDJin
 * @CreationDate 2023/8/7 20:21
 * @Description ：
 */
public class AlertManager {
    private  BlockingQueue<AlertTask> queue ;
    private  DataCenter dataCenter;
    private AlertTaskConsumer consumer;
    /**
     * 初始化报警服务
     *
     * @param center
     */
    public  void initAlert(DataCenter center) {
        dataCenter=center;
        AlertConfig alertConfig = dataCenter.getConfig().getAlertConfig();
        queue = new LinkedBlockingQueue<>(alertConfig.getQueueCapacity());
        WxMsSender.token = alertConfig.getWxToken();
        WxMsSender.wxAppid = alertConfig.getWxAppid();
        WxMsSender.wxSecret = alertConfig.getWxSecret();
        WxMsSender.wxTemplateId = alertConfig.getWxTemplateId();
        NoteSender.accessKey = alertConfig.getAccessKey();
        NoteSender.accessKeySecret = alertConfig.getAccessKeySecret();
        NoteSender.signName = alertConfig.getSignName();
        NoteSender.regionId = alertConfig.getRegionId();
        NoteSender.templateCode = alertConfig.getTemplateCode();
        MailSender.mailUser = alertConfig.getMailUser();
        MailSender.mailPassword = alertConfig.getMailPassword();
        consumer=  new AlertTaskConsumer(this);
        consumer.start();
    }

    /**
     * 添加报警任务
     * @param task
     * @throws InterruptedException
     */
    public  void addAlertTask(AlertTask task) throws InterruptedException {
        queue.put(task);
    }

    /**
     *
     * @param taskId
     * @param data
     * @param dataName
     * @throws InterruptedException
     * @throws IllegalAccessException
     */
    public  void alertJudge(int taskId, MetricData data, String dataName) throws InterruptedException, IllegalAccessException {
        //判断数据是否需要报警
        ConcurrentHashMap<Field, AlertRule> map = dataCenter.getConfig().getMetricAlertConfig().get(taskId);
        if (map != null) {
            for (Map.Entry<Field, AlertRule> entry : map.entrySet()) {
                AlertTask alertTask = entry.getValue().getAlertTask(entry.getKey().get(data));
                if (alertTask != null) {
                    alertTask.setTaskId(dataCenter.getTaskId().getAndIncrement());
                    alertTask.setTaskName(dataName + "数据报警任务" + alertTask.getTaskId());
                    alertTask.setTaskDescription("负责" + dataName + "数据报警通知");
                    addAlertTask(alertTask);
                }
            }

        }
    }

    /**
     * 关闭消费者线程
     */
    public void destroy(){
        consumer.shutdown();
    }
    public DataCenter getDataCenter() {
        return dataCenter;
    }

    public BlockingQueue<AlertTask> getQueue() {
        return queue;
    }
}
