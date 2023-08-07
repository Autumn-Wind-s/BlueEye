package blueeye.wx;

import java.util.Map;


/**
 * @Description 微信公众号模板消息请求对象
 * @author isymi
 * @version
 * @date 2023年5月29日下午4:28:09
 *
 */
public class TemplateMessage {

    /**
     * 发送消息用户的openid
     */
    private String touser;
    /*
     * 模板消息id
     */
    private String template_id;
    /**
     * 点击模板信息跳转地址；置空：则在发送后，点击模板消息会进入一个空白页面（ios），或无法点击（android）
     */
    private String url;
    /**
     * 卡片顶部颜色
     */
    private String topcolor;

    /**
     * key为模板中参数内容"xx.DATA"的xx,value为参数对应具体的值和颜色
     */
    private Map<String, WeChatTemplateMsg> data;
    // private String data;

    public TemplateMessage() {
    }

    public Map<String, WeChatTemplateMsg> getData() {
        return data;
    }

    public void setData(Map<String, WeChatTemplateMsg> data) {
        this.data = data;
    }

    public TemplateMessage(String touser, String template_id, String url, String topcolor, Map<String, WeChatTemplateMsg> data) {
        this.touser = touser;
        this.template_id = template_id;
        this.url = url;
        this.topcolor = topcolor;
        this.data = data;
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String gettemplate_id() {
        return template_id;
    }

    public void settemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTopcolor() {
        return topcolor;
    }

    public void setTopcolor(String topcolor) {
        this.topcolor = topcolor;
    }


    @Override
    public String toString() {
        return "TemplateMessage [touser=" + touser + ", template_id=" + template_id + ", url=" + url + ", topcolor="
                + topcolor + ", data=" + data + "]";
    }

}
