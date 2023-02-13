package com.container;

import com.pojo.alert.AlertRecord;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 17:28
 * @Description ：
 */
public class AlertRecordContainer {
    ConcurrentLinkedDeque<AlertRecord> records;
}
