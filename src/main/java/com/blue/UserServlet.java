package com.blue; /**
 * @Author SDJin
 * @CreationDate 2022/11/2 19:56
 * @Description ：
 */

import com.annotation.RequestEye;
import com.metrics.RequestMetrics;
import com.monitor.request.RequestMonitor;
import com.wujiuye.flow.FlowHelper;
import com.wujiuye.flow.FlowType;
import com.wujiuye.flow.Flower;
import com.wujiuye.flow.common.MetricBucket;
import com.wujiuye.flow.common.WindowWrap;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;
@Deprecated
@WebServlet(name = "UserServlet", value = "/UserServlet")
public class UserServlet extends HttpServlet {

    FlowHelper helper=new FlowHelper(FlowType.Minute);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("user");
        ArrayDeque<Integer> deque=new ArrayDeque<>();

        RequestMetrics take = RequestMonitor.getInstance().take("/UserServlet");
        Flower flow = take.getFlow(FlowType.Hour);
        List<WindowWrap<MetricBucket>> windows = flow.windows();
        for (WindowWrap<MetricBucket> window : windows) {
            System.out.print("窗口"+window.windowStart()+"  ");
            System.out.println(window.value().success());
        }
        System.out.println("总数"+take.getFlow(FlowType.Minute).total());
//
   }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}
