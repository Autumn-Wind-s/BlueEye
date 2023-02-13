package com.monitor.request; /**
 * @Author SDJin
 * @CreationDate 2022/11/3 20:38
 * @Description ：
 */

import com.wujiuye.flow.common.TimeUtil;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@WebFilter(filterName = "RequestFilter")
public class RequestFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("qps过滤器初始化");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String requestURI = req.getRequestURI();
        requestURI = requestURI.replaceFirst(req.getContextPath(), "");
        System.out.println("接口被访问" + requestURI);
        long startTime = TimeUtil.currentTimeMillis();
        chain.doFilter(request, response);
        long rt = TimeUtil.currentTimeMillis() - startTime;
        int status = ((HttpServletResponse) response).getStatus();
        if (status > 400) {
            RequestMonitor.getInstance().take(requestURI).incrException();
        } else {
            RequestMonitor.getInstance().take(requestURI).incrSuccess(rt);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
