package com.wisp.core.web.base;

import com.wisp.core.cache.CacheHander;
import com.wisp.core.utils.MVCExceptionHandle;
import com.wisp.core.vo.ResponseResultVo;
import com.wisp.game.bet.recharge.common.UicCacheKey;
import com.wisp.game.bet.recharge.dao.info.CacheUserInfo;
import org.apache.oltu.oauth2.common.OAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * 控制器支持类
 *
 * @author Ares
 * @version 2016-01-23
 */
public abstract class BaseController extends MVCExceptionHandle {
    //public static final BaseRspBean SUCCESS = new SuccessRspBean<>(), ERROR = new ErrorRspBean();

    @Autowired
    protected CacheHander cacheHander;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * data
     *
     * @param data
     * @return
     */
    public ResponseResultVo<?> data(Object data) {
        ResponseResultVo responseResultVo = new ResponseResultVo();
        responseResultVo.setData(data);
        responseResultVo.setMsg("ok");
        responseResultVo.setCode(200);
        return responseResultVo;
    }

    public ResponseResultVo<?> emptySucc() {
        ResponseResultVo responseResultVo = new ResponseResultVo();
        responseResultVo.setData(new HashMap<>());
        responseResultVo.setMsg("ok");
        responseResultVo.setCode(200);
        return responseResultVo;
    }

    /**
     * 错误
     *
     * @param message
     * @return
     */
    public ResponseResultVo error(String message) {
        ResponseResultVo responseResultVo = new ResponseResultVo();
        responseResultVo.setCode(101);
        responseResultVo.setMsg(message);
        return responseResultVo;
    }

    public ResponseResultVo error(int code) {
        ResponseResultVo responseResultVo = new ResponseResultVo();
        responseResultVo.setCode(code);
        responseResultVo.setMsg("");
        return responseResultVo;
    }

    public ResponseResultVo error(BaseErrorCode code) {
        ResponseResultVo responseResultVo = new ResponseResultVo();
        responseResultVo.setCode(code.getCode());
        responseResultVo.setMsg("");
        return responseResultVo;
    }

    /**ErrorRspBean
     * 错误
     *
     * @param message
     * @return
     */
    public ResponseResultVo error(int code, String message) {
        ResponseResultVo responseResultVo = new ResponseResultVo();
        responseResultVo.setCode(code);
        responseResultVo.setMsg(message);
        return responseResultVo;
    }

    public ResponseResultVo error(BaseErrorCode code, String message) {
        ResponseResultVo responseResultVo = new ResponseResultVo();
        responseResultVo.setCode(code.getCode());
        responseResultVo.setMsg(message);
        return responseResultVo;
    }



    /**
     * 获得Ip地址
     *
     * @return
     */
    protected String getIp() {
        return getRequest().getLocalAddr();
    }



    /**
     * 获取用户信息
     *
     * @param request
     * @return
     */


    /**
     * 获取token，不抛出未登录异常
     *
     * @return
     */
    protected String getToken() {
        return getRequest().getHeader(OAuth.HeaderType.AUTHORIZATION);
    }


    /**
     * 获取request
     *
     * @return
     */
    public HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public CacheUserInfo getCacheUserInfo()
    {
        String token = getToken();
        if(  token == null || "".equals(token))
        {
            return null;
        }

        String cache_key = UicCacheKey.OAUTH2_TOKEN_INFO.key(token);

        CacheUserInfo cacheUserInfo =  cacheHander.get(cache_key);

        return cacheUserInfo;
    }


    protected String appendUrlParam(String url, String param) {
        return url + (url.contains("?") ? "&" : "?") + param;
    }
}
