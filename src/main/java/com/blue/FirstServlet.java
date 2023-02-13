package com.blue;

import com.annotation.RequestEye;
import com.metrics.Metrics;
import com.pojo.metric.MetricsType;
import com.monitor.request.RequestMonitor;
import com.util.ClassUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author SDJin
 * @CreationDate 2022/11/2 19:52
 * @Description ：
 */
@WebServlet(name = "FirstServlet", value = "/first",loadOnStartup = 1)
public class FirstServlet extends HttpServlet {
    Map<MetricsType, Map<String, Metrics>> map=new ConcurrentHashMap<>();
    private static final List<Class<? extends Annotation>> EYE_ANNOTATION = Arrays.asList(
            RequestEye.class);


    @Override
    public void init() throws ServletException {
        System.out.println("初始化中");
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("blueEye.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //根据包扫描路径获得所有类对象，根据反射将加有监控注解的所有对象加到对应的监控池中
        String packageName = (String) properties.get("scanPackageName");
        Set<Class<?>> classSet = ClassUtil.extractPackageClass(packageName);
        for (Class<?> aClass : classSet) {
            for (Method method : aClass.getDeclaredMethods()) {
                for (Class<? extends Annotation> annotation : EYE_ANNOTATION) {
                    if (method.isAnnotationPresent(annotation)){
                        //当扫描到Eye注解后将对应注解和方法加入到对应监控器的监控池中
                        doMonitor(method.getDeclaredAnnotation(annotation),method);  ;
                    }
                }
            }
        }
        //初始化不同的监控器
        RequestMonitor.getInstance().monitoring(getServletContext());

    }

    /**
     * 将监控指标加入到对应类型的监控池中
     * @param annotation
     * @param method
     */
    void doMonitor(Annotation annotation ,Method method){
        if (annotation instanceof RequestEye ){
            System.out.println("requestEye匹配成功");
            RequestMonitor.getInstance().addMetrics((RequestEye) annotation,method);
        }

    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("接受请求");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        resp.getWriter().write("你好");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        super.service(req, res);
    }
}
