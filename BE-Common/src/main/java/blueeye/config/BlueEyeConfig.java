package blueeye.config;


import blueeye.pojo.user.User;
import lombok.Data;

import java.util.List;

/**
 * @Author SDJin
 * @CreationDate 2023/2/8 16:09
 * @Description ：
 */
@Data
public class BlueEyeConfig {

    List<User> users;
    String scanPackageName;
    String rdbPath;
    Integer rdbFileNum;
    Integer persistenceMode;
    String properties;
    String manager;
    String alertMapper;
    String instanceMapper;
    String metricMapper;
    String taskMapper;
    Long timeSpan;
    Integer wheelSize;
    Integer coreThread;
    Integer retryNum;

}
