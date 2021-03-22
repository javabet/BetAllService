package com.wisp.game.bet.api.interceptor;

import com.wisp.game.bet.api.exceptions.CustomException;
import com.wisp.game.bet.api.vo.ResponseResultVo;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CommonExceptionHandler
{
    @ExceptionHandler(CustomException.class)
    public ResponseResultVo exceptionHandler(HttpServletRequest request, Exception e){
        //绑定异常是需要明确提示给用户的
        if(e instanceof CustomException){
            CustomException exception=(CustomException) e;
            return ResponseResultVo.failure(exception.getCode());
        }
// 其余异常简单返回为服务器异常
        return ResponseResultVo.failure(0);

    }
}
