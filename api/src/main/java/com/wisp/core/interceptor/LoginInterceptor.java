package com.wisp.core.interceptor;

import com.google.gson.Gson;
import com.wisp.core.cache.CacheHander;
import com.wisp.core.constants.RedisCommonEnum;
import com.wisp.core.service.ResponseResult;
import com.wisp.core.vo.ResponseResultVo;
import com.wisp.game.bet.recharge.common.ErrorCode;
import org.apache.oltu.oauth2.common.OAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginInterceptor implements HandlerInterceptor
{
    @Autowired
    private CacheHander redisCache;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!( handler instanceof HandlerMethod))
        {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod)handler;
        Method method = handlerMethod.getMethod();

        //不需要登陆的接口，直接放行
        if( method.getAnnotation( SkipAuthToken.class ) != null || handlerMethod.getBeanType().getAnnotation(SkipAuthToken.class) != null )
        {
            return true;
        }


        String token =  request.getHeader(OAuth.HeaderType.AUTHORIZATION);
        if( token == null || token.trim().equals("") )
        {
            //response.sendRedirect("/redirect/index");
            System.out.println("没有登陆，请先登陆");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ResponseResultVo responseResult = new ResponseResultVo();
            responseResult.setCode(ErrorCode.ERR_USER_NAME.getCode());
            responseResult.setMsg("login first please");
            response.getWriter().write(new Gson().toJson(responseResult));
            return false;
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
