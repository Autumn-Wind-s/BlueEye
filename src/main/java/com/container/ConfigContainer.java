package com.container;

import com.pojo.alert.AlertRule;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 17:27
 * @Description ：
 */
public class ConfigContainer {
    private BlueEyeConfig blueEyeconfig;
    private AtomicLong instancePersistence;
    private ConcurrentHashMap<Integer,Long> metricPersistence;
    private ConcurrentHashMap<Integer, ConcurrentHashMap<String, AlertRule>> metricAlertConfig;


}
