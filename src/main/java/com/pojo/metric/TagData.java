package com.pojo.metric;

import com.pojo.po.MetricDataPo;
import lombok.Data;

/**
 * @Author SDJin
 * @CreationDate 2023/2/6 21:06
 * @Description ：
 */
@Data
public class TagData<T> extends MetricData {
    T data;

    public TagData(T data) {
        this.data = data;
    }

    /**
     * 通过构造器的方式实现反压缩
     * @param dataPo
     */
    public TagData(MetricDataPo dataPo) {
        //todo 反压缩逻辑
    }

    @Override
    public MetricDataPo compress() {
        //todo 压缩逻辑
        return null;
    }
}
