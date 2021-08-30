package com.wisp.core.interceptor;

import com.wisp.core.constants.Constant;
import com.wisp.core.manager.ErrorCodeManager;
import com.wisp.core.vo.ResponseResultVo;
import com.wisp.core.web.base.BaseErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/***
 * 拦截Controller方法默认返回参数，统一处理返回值/响应体
 */
//@ControllerAdvice
@RestControllerAdvice
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

        if(body instanceof ResponseResultVo) {
            responseResultVo = (ResponseResultVo)body;
        }
        else if(body instanceof BaseErrorCode)
        {
            BaseErrorCode baseErrorCode = (BaseErrorCode)body;
            responseResultVo = ResponseResultVo.failure(baseErrorCode.getCode());
        }
        else
        {
            // 包装返回体
            responseResultVo = ResponseResultVo.success(body);
        }

        if( "".equals(responseResultVo.getMsg()) || responseResultVo.getMsg() == null )
        {
            responseResultVo.setMsg( errorCodeManager.getErrMsg(responseResultVo.getCode()) );
        }

        return responseResultVo;
    }

}
