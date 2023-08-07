package blueeye.filter;
/**
 * @Author SDJin
 * @CreationDate 2022/11/3 20:38
 * @Description ：
 */

import blueeye.context.BlueEyeContext;
import com.wujiuye.flow.common.TimeUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "RequestFilter")
@Slf4j
public class RequestFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("qps过滤器初始化");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String requestURI = req.getRequestURI();
        requestURI = requestURI.replaceFirst(req.getContextPath(), "");
        log.info("接口被访问" + requestURI);
        long startTime = TimeUtil.currentTimeMillis();
        chain.doFilter(request, response);
        long rt = TimeUtil.currentTimeMillis() - startTime;
        int status = ((HttpServletResponse) response).getStatus();
        if (status > 400) {
            BlueEyeContext.dataCenter.uploadInterfaceData(requestURI,rt,false);
        } else {
            BlueEyeContext.dataCenter.uploadInterfaceData(requestURI,rt,true);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
