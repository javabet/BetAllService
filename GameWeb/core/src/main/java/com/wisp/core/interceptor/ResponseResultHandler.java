package com.wisp.core.interceptor;

import com.wisp.core.constants.Constant;
import com.wisp.core.manager.ErrorCodeManager;
import com.wisp.core.vo.ResponseResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ResponseResultHandler implements ResponseBodyAdvice<Object>
{
    @Autowired
    private ErrorCodeManager errorCodeManager;

    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass)
    {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        /* 判断是否需要统一包装，如果attribute中有，则表示需要处理，返回true */
        Object annotation = request.getAttribute(Constant.RESPONSE_RESULT_ANNOTATION);
        return !(annotation == null);
    }

    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse)
    {
        ResponseResultVo responseResultVo;

        if(!(body instanceof ResponseResultVo)) {
            // 包装返回体
            responseResultVo = ResponseResultVo.success(body);
        }
        else
        {
            responseResultVo = (ResponseResultVo)body;
        }

        if( "".equals(responseResultVo.getMsg()) || responseResultVo.getMsg() == null )
        {
            responseResultVo.setMsg( errorCodeManager.getErrMsg(responseResultVo.getCode()) );
        }

        return responseResultVo;
    }
}
