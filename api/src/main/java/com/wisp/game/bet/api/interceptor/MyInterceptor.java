package com.wisp.game.bet.api.interceptor;


import org.aopalliance.intercept.Interceptor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class MyInterceptor extends HandlerInterceptorAdapter
{
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        StringBuffer requestURL = request.getRequestURL();
        System.out.println("前置拦截器1 preHandle： 请求的uri为："+requestURL.toString());
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        System.out.println("拦截器1 postHandle： ");
    }
}
