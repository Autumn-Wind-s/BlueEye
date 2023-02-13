package com.monitor;

import com.metrics.RequestMetrics;

import java.util.LinkedHashMap;

/**
 * @Author SDJin
 * @CreationDate 2022/11/2 21:01
 * @Description ：
 */
public abstract class Monitor<T >{
    /**
     * 监控池，后期可用对应的监控池代替
     */
     protected LinkedHashMap<String, T> map;
     public abstract void  monitoring(Object object);

}
