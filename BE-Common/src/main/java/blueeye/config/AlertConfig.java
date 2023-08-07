package blueeye.config;

import lombok.Data;

/**
 * @Author SDJin
 * @CreationDate 2023/6/28 15:24
 * @Description ：
 */
@Data
public class AlertConfig {
    //wx配置

    String wxToken;
    String wxAppid;
    String wxSecret;
    String wxTemplateId;
    //note配置

    String accessKey;
    String accessKeySecret;
    String signName;
    String templateCode;
    String regionId;
    //mail配置

    String mailUser;
    String mailPassword;

    String number;
    Integer queueCapacity;
}
