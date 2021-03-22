package com.wisp.game.bet.api.manager;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ErrorCodeManager implements InitializingBean
{
    private Map<Integer,String> msgCodeMap = new HashMap<Integer, String>();
    public ErrorCodeManager()
    {

    }

    public void putErrMsg(int code,String msg)
    {
        msgCodeMap.put(code,msg);
    }

    public String getErrMsg(int code)
    {
        if( !msgCodeMap.containsKey(code) )
        {
            return "";
        }

        return msgCodeMap.get(code);
    }

    public     void afterPropertiesSet() throws Exception
    {
        msgCodeMap.put(200,"ok");
        msgCodeMap.put(10001,"什么鬼");
    }

}
