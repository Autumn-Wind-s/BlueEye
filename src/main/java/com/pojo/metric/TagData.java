package com.pojo.metric;

import lombok.Data;

/**
 * @Author SDJin
 * @CreationDate 2023/2/6 21:06
 * @Description ：
 */
@Data
public class TagData<T> extends MetricData{
    T data;

    public TagData(T data) {
        this.data = data;
    }

}
