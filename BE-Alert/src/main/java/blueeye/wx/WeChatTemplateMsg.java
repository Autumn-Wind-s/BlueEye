package blueeye.wx;


import java.io.Serializable;

/**
 * @Description 模板消息内容类
 * @author isymi
 * @version
 * @date 2023年5月29日下午4:33:27
 *
 */
public class WeChatTemplateMsg implements Serializable{

    /**
     * 消息实参
     */
    private String value;
    /**
     * 消息颜色
     */
    private String color;


    public WeChatTemplateMsg(String value) {
        this.value = value;
        this.color = "#173177";
    }

    public WeChatTemplateMsg(String value, String color) {
        this.value = value;
        this.color = color;
    }

    @Override
    public String toString() {
        return "WeChatTemplateMsg [value=" + value + ", color=" + color + "]";
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


}
