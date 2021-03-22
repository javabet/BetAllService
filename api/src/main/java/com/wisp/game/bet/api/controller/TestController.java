package com.wisp.game.bet.api.controller;

import com.wisp.game.bet.api.exceptions.CustomException;
import com.wisp.game.bet.api.info.UserInfo;
import com.wisp.game.bet.api.service.ResponseResult;
import com.wisp.game.bet.api.vo.ResponseResultVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@ResponseResult
public class TestController
{
    @RequestMapping("/test")
    public Object test()
    {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("key","go this");
        boolean debug = true;
        if( debug )
        {
            //throw  new CustomException(1);
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setAge(11);
        userInfo.setDate(new Date());

        return userInfo;
    }
}
