package com.metrics;

import com.util.ClassUtil;
import com.wujiuye.flow.FlowHelper;
import com.wujiuye.flow.FlowType;
import com.wujiuye.flow.Flower;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author SDJin
 * @CreationDate 2022/11/2 21:15
 * @Description ：
 */
@Deprecated
public class RequestMetrics implements Metrics{
    /**
     *     适配器模式
     */

    private FlowHelper helper;

    public RequestMetrics(FlowType... types ) {
        this.helper = new FlowHelper(types);
    }

    public void incrSuccess(long rt){
        helper.incrSuccess(rt);
    }
    public void incrException(){
        helper.incrException();
    }
    public Flower getFlow(FlowType flowType){
        return helper.getFlow(flowType);
    }
}
