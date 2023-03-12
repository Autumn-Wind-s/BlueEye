package com.pojo.metric;

import com.pojo.po.MetricDataPo;

/**
 * @Author SDJin
 * @CreationDate 2023/2/6 20:54
 * @Description ：
 */
public class InterfaceData extends MetricData{
    /**
     * 通过构造器的方式实现反压缩
     * @param dataPo
     */
    public InterfaceData (MetricDataPo dataPo){
        //todo 反压缩逻辑

    }

    @Override
    public MetricDataPo compress() {
        //todo 压缩逻辑

        return null;
    }
}
