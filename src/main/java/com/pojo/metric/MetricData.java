package com.pojo.metric;

import com.pojo.Compression.CompressionInterface;
import com.pojo.po.MetricDataPo;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 17:21
 * @Description ：
 */
@Data
public abstract class MetricData implements CompressionInterface<MetricDataPo> {
   protected Integer id;
   protected Integer taskId;
   protected Integer instanceId;
   protected Timestamp createTime;


}
