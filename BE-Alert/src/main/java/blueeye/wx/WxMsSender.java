package blueeye.wx;

import blueeye.pojo.task.impl.alert.AlertTask;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author SDJin
 * @CreationDate 2023/6/27 9:11
 * @Description ：
 */
public class WxMsSender {
    /**
     * 以下属性需用户在配置文件中配置
     */
    //必须替换
    public static String token;
    // 必须替换
    public static String wxAppid;
    // 必须替换
    public static String wxSecret;
    // 必须替换
    public static String wxTemplateId;
    /**
     * 全局AccessToken
     */
    private static AccessToken wxToken;

    /**
     *
     * @param task
     * @return 发送消息的响应码，记录到报警记录的content中
     * @throws IOException
     */
    public static String doSendMessage(AlertTask task) throws IOException {
        String token = getToken();
        String url = "https://api.weixin.qq.com/cgi-bin/user/get?" + "access_token=" + token;
        TemplateMessage templateMessage = new TemplateMessage();
        templateMessage.setTouser(task.getNotifier());
        templateMessage.settemplate_id(wxTemplateId);
        templateMessage.setTopcolor("#FF0000");
        templateMessage.setData(JSON.parseObject(task.getContent(), Map.class)
        );
        return WXPublicAccountHttpUtil.sendMessage(getToken(), templateMessage);

    }

    /**
     * @return
     * @Description 获取token, 本地缓存有就直接返回，没有就发送请求获取（wx官方api获取token每天有限制，因此需做缓存)
     * @date 2023年5月29日下午4:13:17
     */
    public static String getToken() {
        if (wxToken == null || wxToken.isExpired()) {
            getAccessToken();
        }
        return wxToken.getAccessToken();
    }

    /**
     * 给AccessToken赋值
     */
    private static void getAccessToken() {

        // 发送请求获取token
        String token = null;
        try {
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential" + "&appid=" + wxAppid + "&secret=" + wxSecret;
            token = WXPublicAccountHttpUtil.get(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = JSONObject.parseObject(token);
        String accessToken = (String) jsonObject.get("access_token");
        Integer expiresIn = (Integer) jsonObject.get("expires_in");
        // 创建token对象，并存储
        wxToken = new AccessToken(accessToken, String.valueOf(expiresIn));
        System.out.println(token);
    }


}