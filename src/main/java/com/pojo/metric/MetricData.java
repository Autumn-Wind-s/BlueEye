package com.pojo.metric;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 17:21
 * @Description ：
 */
@Data
public class MetricData {
   protected Integer id;
   protected Integer taskId;
   protected Integer instanceId;
   protected Timestamp createTime;
}
