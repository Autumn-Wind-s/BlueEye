package blueeye.controller;

import blueeye.pojo.task.impl.alert.AlertTask;
import blueeye.wx.WxMsSender;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author SDJin
 * @CreationDate 2023/6/27 10:49
 * @Description ：
 */

public class AlertController {

    /**
     * 需根据用户的需求配置容量
     */
    public static BlockingQueue<AlertTask> queue ;


    //    @RequestMapping("wx")
    public void doGetWx(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");

        // 将微信echostr返回给微信服务器
        try (OutputStream os = response.getOutputStream()) {
            String sha1 = getSHA1(WxMsSender.getToken(), timestamp, nonce, "");

            // 和signature进行对比
            if (sha1.equals(signature)) {
                // 返回echostr给微信
                os.write(URLEncoder.encode(echostr, "UTF-8").getBytes());
                os.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 用SHA1算法生成安全签名
     *
     * @param token     票据
     * @param timestamp 时间戳
     * @param nonce     随机字符串
     * @param encrypt   密文
     * @return 安全签名
     * @throws Exception
     */
    public static String getSHA1(String token, String timestamp, String nonce, String encrypt) throws Exception {
        try {
            String[] array = new String[] { token, timestamp, nonce, encrypt };
            StringBuffer sb = new StringBuffer();
            // 字符串排序
            Arrays.sort(array);
            for (int i = 0; i < 4; i++) {
                sb.append(array[i]);
            }
            String str = sb.toString();
            // SHA1签名生成
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(str.getBytes());
            byte[] digest = md.digest();
            StringBuffer hexstr = new StringBuffer();
            String shaHex = "";
            for (int i = 0; i < digest.length; i++) {
                shaHex = Integer.toHexString(digest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexstr.append(0);
                }
                hexstr.append(shaHex);
            }
            return hexstr.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
