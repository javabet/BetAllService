package com.wisp.game.bet.api.interceptor;

import com.wisp.game.bet.api.constants.Constant;
import com.wisp.game.bet.api.service.BaseControllerAnnotation;
import com.wisp.game.bet.api.service.ResponseResult;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Service
public class ResponseResultInterceptor  extends HandlerInterceptorAdapter
{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        if(handler instanceof HandlerMethod) {
            /* 反射获取类和方法 */
            final HandlerMethod handlerMethod = (HandlerMethod) handler;
            final Class<?> clazz = handlerMethod.getBeanType();
            final Method method = handlerMethod.getMethod();
            /* 校验类的注解和方法的注解，满足条件后，添加到attribute */
            if(clazz.isAnnotationPresent(ResponseResult.class)) {
                request.setAttribute(Constant.RESPONSE_RESULT_ANNOTATION, clazz.getAnnotation(ResponseResult.class));
            } else if(method.isAnnotationPresent(ResponseResult.class)) {
                request.setAttribute(Constant.RESPONSE_RESULT_ANNOTATION, method.getAnnotation(ResponseResult.class));
            } else if(clazz.isAnnotationPresent(BaseControllerAnnotation.class)) {
                request.setAttribute(Constant.RESPONSE_RESULT_ANNOTATION, clazz.getAnnotation(BaseControllerAnnotation.class));
            }
        }

        return super.preHandle(request, response, handler);
    }
}
