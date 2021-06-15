package com.wisp.core.interceptor;

import com.wisp.core.cache.CacheHander;
import com.wisp.core.constants.RedisCommonEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor
{
    @Autowired
    private CacheHander redisCache;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //不需要登陆的接口，直接放行
        String uri = request.getRequestURI().toString();
        if( uri.equals("/system/login") || uri.equals("/redirect/index") || uri.equals("/error") )
        {
            return true;
        }

        String token =  request.getHeader("Token");
        if( token == null || token.trim().equals("") )
        {
            //response.sendRedirect("/redirect/index");
            System.out.println("没有登陆，请先登陆");
            return true;
        }

        //在redis中判断是否有此token,
        boolean existFlag = redisCache.exists(RedisCommonEnum.AUTH_KEY_PREFIX.getKey());
        if( !existFlag )
        {
            return false;
        }

        return true;
    }
}
