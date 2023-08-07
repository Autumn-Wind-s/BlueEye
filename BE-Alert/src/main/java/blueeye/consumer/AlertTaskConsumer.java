package blueeye.consumer;

import blueeye.email.MailSender;
import blueeye.manager.AlertManager;
import blueeye.note.NoteSender;
import blueeye.pojo.alert.AlertMethod;
import blueeye.pojo.alert.AlertRecord;
import blueeye.pojo.task.impl.alert.AlertTask;
import blueeye.wx.WxMsSender;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.concurrent.ExecutionException;

/**
 * @Author SDJin
 * @CreationDate 2023/6/27 15:20
 * @Description ：
 */
public class AlertTaskConsumer extends Thread {
    private AlertManager manager;
    private boolean running = true;
    public AlertTaskConsumer(AlertManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        while (running) {
            AlertTask task =null;
            try {
                task = manager.getQueue().take();
            } catch (InterruptedException e) {
                //线程阻塞被打断，继续开始
                continue;
            }
            //从数据中心获取自增ID
            AlertRecord alertRecord = new AlertRecord(1, task.getTaskId());
            alertRecord.setAlertMethod(task.getMethod());
            alertRecord.setNotifier(task.getNotifier());
            AlertMethod method = task.getMethod();
            try {
                if (method.equals(AlertMethod.WX)){
                    alertRecord.setAlertTime(new Timestamp(System.currentTimeMillis()));
                    alertRecord.setContent("报警内容："+task.getContent()+"\n操作结果："+ WxMsSender.doSendMessage(task));
                } else if (method.equals(AlertMethod.NOTE)) {
                    alertRecord.setAlertTime(new Timestamp(System.currentTimeMillis()));
                    alertRecord.setContent("报警内容："+task.getContent()+"\n操作结果："+ NoteSender.doSendMessage(task));
                }else if(method.equals(AlertMethod.EMail)){
                    alertRecord.setAlertTime(new Timestamp(System.currentTimeMillis()));
                    alertRecord.setContent("报警内容："+task.getContent()+"\n操作结果："+ MailSender.doSendMessage(task));
                }else {
                    // todo 电话报警
                }
            } catch (InterruptedException | IOException | ExecutionException e) {
                alertRecord.setContent("报警内容："+task.getContent()+"\n操作结果："+ e.getMessage());
            }
            //  将AlertRecord添加到数据中心
           manager.getDataCenter().getRecords().addAlertRecord(alertRecord);
        }
    }
    public void shutdown(){
        running=false;
    }
}
