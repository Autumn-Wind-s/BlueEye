package com.pojo.alert;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Author SDJin
 * @CreationDate 2023/2/4 14:01
 * @Description ：
 */
public class AlertRule {
    private Condition condition;
    private List<Object> values;
    private Timestamp recentAlertTime;
    private Long intervalTime;
    private Integer currLevel;
    private Long switchTime;
    private List<AlertAction> actions;
}
