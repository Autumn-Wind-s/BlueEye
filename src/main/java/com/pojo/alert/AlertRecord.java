package com.pojo.alert;

import com.pojo.Compression.CompressionInterface;
import com.pojo.po.AlertRecordPo;

/**
 * @Author SDJin
 * @CreationDate 2023/2/3 17:25
 * @Description ：
 */
public class AlertRecord implements CompressionInterface<AlertRecordPo> {
    /**
     * 通过构造器实现反压缩
     * @param alertRecordPo
     */
    public AlertRecord(AlertRecordPo alertRecordPo){
        //todo 反压缩逻辑
    }
    @Override
    public AlertRecordPo compress() {
        //todo 压缩逻辑
        return null;
    }
}
