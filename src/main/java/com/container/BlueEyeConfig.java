package com.container;

import com.pojo.User;

import java.util.List;

/**
 * @Author SDJin
 * @CreationDate 2023/2/8 16:09
 * @Description ：
 */
public class BlueEyeConfig {

    List<User> users;
    String scanPackageName;
    String rdbPath;
    String rdbFileNum;
    Integer persistenceMode;
    String alertMapper;
    String instanceMapper;
    String metricMapper;
    String taskMapper;
    Long timeSpan;
    Integer size;
    Integer coreThread;
    Integer retryNum;
    List<String> monitorTasks;

}
