package com.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.reggie.common.Result;
import com.reggie.common.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录拦截器
 */
@WebFilter("/*")
@Slf4j
public class LoginFilter implements Filter {
    public static final AntPathMatcher pathMatcher=new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest=(HttpServletRequest)servletRequest;
        HttpServletResponse httpServletResponse=(HttpServletResponse)servletResponse;
        Object employeeId = httpServletRequest.getSession().getAttribute("employeeId");
        //log.info("已拦截请求:"+httpServletRequest.getRequestURL());
        /*
1、获取本次请求的URL
2、判断本次请求是否需要处理
3、如果不需要处理，则直接放行
4、判断登录状态，如果已登录，则直接放行
5、如果未登录则返回未登录结果
         */
        //获取本次请求的URI
        String requestURI = httpServletRequest.getRequestURI();
        log.info("本次请求的URI为{}",requestURI);
        String[] uris=new String[]{
            "/employee/login",
            "/employee/logout",
            "/backend/**",
            "/front/**",
             "/user/login",
              "/user/sendMsg"
        };
        /*
        判断本次请求是否需要处理
        如果不需要处理，则直接放行
         */
        if(check(uris,requestURI)) {
            log.info("放行成功,本次请求不需要处理,本次请求的URI为"+requestURI);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
             return;
        }
       //判断登录状态(后台)，如果已登录，则直接放行
        if (employeeId!=null){
            log.info("账号已登录 id为{}",httpServletRequest.getSession().getAttribute("employeeId"));
            log.info("线程id:"+Thread.currentThread().getId());//判断登录后是不是同一个线程id
            long id = (long)httpServletRequest.getSession().getAttribute("employeeId");
            ThreadLocalUtil.setCurrentId(id);
            filterChain.doFilter(httpServletRequest,httpServletResponse);
             return;
        }
        //判断登录状态(程序端)，如果已登录，则直接放行
        if (httpServletRequest.getSession().getAttribute("user")!=null){
            log.info("账号已登录 id为{}",httpServletRequest.getSession().getAttribute("user"));
            log.info("线程id:"+Thread.currentThread().getId());//判断登录后是不是同一个线程id
            long id = (long)httpServletRequest.getSession().getAttribute("user");
            ThreadLocalUtil.setCurrentId(id);
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }
        //如果未登录则返回未登录结果
            log.info("账号未登录");
            //响应给前端
            httpServletResponse.getWriter().write(JSON.toJSONString(Result.Error("NOTLOGIN")));
             return;

    }

    /**
     * 路径匹配
     * @param uris
     * @param URI
     * @return
     */
    public boolean check(String[] uris,String URI){
        for (String uri:
             uris) {
            boolean match = pathMatcher.match(uri, URI);
            if(match)
                return true;
        }
        return false;
    }
}
