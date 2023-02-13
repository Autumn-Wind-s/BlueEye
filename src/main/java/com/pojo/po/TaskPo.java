package com.pojo.po;

import java.sql.Timestamp;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 16:31
 * @Description ：
 */
public class TaskPo {
    Integer taskId;
    String taskName;
    String task_description;
    Timestamp create_time;
    Integer type;
    Long cycle;
    String preTask;
    Integer order;
    String json;
}
