package com.monitor.request;

import com.annotation.RequestEye;
import com.metrics.RequestMetrics;
import com.monitor.Monitor;
import com.wujiuye.flow.FlowType;

import javax.servlet.*;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * @Author SDJin
 * @CreationDate 2022/11/2 20:37
 * @Description ：
 */
@Deprecated
public class RequestMonitor extends Monitor<RequestMetrics> {

    /**
     * 单例线程安全RequestMonitor对象
     */
    private static volatile RequestMonitor instance;


    private RequestMonitor() {

    }

    /**
     * 实例化Controller监听器
     *
     * @return 返回ServiceMapperMonitor对象
     */
    public static RequestMonitor getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (RequestMonitor.class) {
            if (instance != null) {
                return instance;
            }
            instance = new RequestMonitor();
            instance.map=new LinkedHashMap<>();
            return instance;
        }
    }

    public synchronized void addMetrics(RequestEye requestEye, Method method) {
        if (requestEye.Minute() && requestEye.hour()) {
            map.put(requestEye.value(), new RequestMetrics(FlowType.Second, FlowType.Minute, FlowType.Hour));
        } else if (requestEye.Minute()) {
            map.put(requestEye.value(), new RequestMetrics(FlowType.Second, FlowType.Minute));
        } else {
            map.put(requestEye.value(), new RequestMetrics(FlowType.Second, FlowType.Hour));
        }
    }

    /**
     * 判断接口监控池是否为空
     *
     * @return 返回true或false
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /***
     * 通过key取出数据
     * @param key 取出数据对应的key
     * @return 监控池中Key对应的RequestMetrics
     */
    public synchronized RequestMetrics take(String key) {
        return map.get(key);
    }


    /***
     * 取出所有key
     * @return 队列的集合中所有key的集合
     */
    public Set<String> getKeys() {
        return map.keySet();
    }

    /**
     * 判断接口路径是否被监控
     * @param key
     * @return
     */
    public boolean isContain(String key){
        return map.containsKey(key);
    }
    @Override
   public void monitoring(Object object) {
        System.out.println("监控指标为"+map.size());
        ServletContext servletContext=(ServletContext) object;
        try {
            RequestFilter filter = servletContext.createFilter(RequestFilter.class);
            servletContext.addFilter("RequestFilter",filter);
        } catch (ServletException e) {
            e.printStackTrace();
        }
        FilterRegistration requestFilter = servletContext.getFilterRegistration("RequestFilter");
        System.out.println(requestFilter);
        for (String s : map.keySet()) {
            requestFilter.addMappingForUrlPatterns(null,false,s);
        }
    }

    /**
     * 请求拦截器,实现对被监控接口的qpe记录
     */

}

