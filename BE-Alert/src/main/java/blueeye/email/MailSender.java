package blueeye.email;
import blueeye.pojo.task.impl.alert.AlertTask;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 *
 * @author song
 */
public  class MailSender {
    /**
     *      发件人称号，同邮箱地址，需用户在配置文件中配置
     */
    public static  String mailUser = "2386537269@qq.com";
    /**
     *     如果是qq邮箱可以使户端授权码，或者登录密码，需用户在配置文件中配置
     */
    public static  String mailPassword = "rnkpsrkkfojaeajj";


    public static boolean doSendMessage(AlertTask task){
        try {
            final Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", "smtp.qq.com");
            // 发件人的账号
            props.put("mail.user", mailUser);
            //发件人的密码
            props.put("mail.password", mailPassword);

            // 构建授权信息，用于进行SMTP进行身份验证
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    // 用户名、密码
                    String userName = props.getProperty("mail.user");
                    String password = props.getProperty("mail.password");
                    return new PasswordAuthentication(userName, password);
                }
            };
            // 使用环境属性和授权信息，创建邮件会话
            Session mailSession = Session.getInstance(props, authenticator);
            // 创建邮件消息
            MimeMessage message = new MimeMessage(mailSession);
            // 设置发件人
            String username = props.getProperty("mail.user");
            InternetAddress form = new InternetAddress(username);
            message.setFrom(form);

            // 设置收件人
            InternetAddress toAddress = new InternetAddress(task.getNotifier());
            message.setRecipient(Message.RecipientType.TO, toAddress);

            // 设置邮件标题
            message.setSubject("BlueEye报警通知");

            // 设置邮件的内容体
            message.setContent(task.getContent(), "text/html;charset=UTF-8");
            // 发送邮件
            Transport.send(message);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }





}
