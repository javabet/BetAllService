package com.wisp.game.core.web.base;

import com.wisp.game.core.web.response.ErrorRespBean;
import com.wisp.game.core.web.response.SuccessRespBean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

    public <T> SuccessRespBean<T> data(T data)
    {
        SuccessRespBean successRespBean = new SuccessRespBean<T>();
        successRespBean.setData(data);

        return successRespBean;
    }

    public <T> SuccessRespBean<T> data(T data,String msg)
    {
        SuccessRespBean successRespBean = new SuccessRespBean<T>();
        successRespBean.setData(data);
        successRespBean.setMessage(msg);
        return successRespBean;
    }

    public ErrorRespBean error(int code)
    {
        ErrorRespBean respBean = error(code,"");
        return  respBean;
    }

    public ErrorRespBean error(int code,String msg)
    {
        ErrorRespBean respBean = new ErrorRespBean();
        respBean.setCode(code);
        respBean.setMessage(msg);
        respBean.setData(new Object());
        return  respBean;
    }


    protected String getIp() {
        return getRequest().getLocalAddr();
    }

    public HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

}
