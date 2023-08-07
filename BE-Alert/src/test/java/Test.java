import blueeye.email.MailSender;
import blueeye.note.NoteSender;
import blueeye.pojo.task.impl.alert.AlertTask;
import blueeye.wx.TemplateMessage;
import blueeye.wx.WeChatTemplateMsg;
import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author SDJin
 * @CreationDate 2023/7/27 9:44
 * @Description ：
 */
public class Test {
    @org.junit.Test
   public void NoteSender() throws Exception {
       AlertTask alertTask = new AlertTask();
       alertTask.setContent("{\"code\":\"1234\"}");
       alertTask.setNotifier("13330124929");
       String s = NoteSender.doSendMessage(alertTask);
       System.out.println(s);
    }
    @org.junit.Test
    public void MailSender(){
        AlertTask alertTask = new AlertTask();
        alertTask.setNotifier("15279293415@163.com");
//        alertTask.setNotifier("2351585664@qq.com");
        alertTask.setContent("你好呀！");
        boolean b = MailSender.doSendMessage(alertTask);
        System.out.println(b);
    }
}
