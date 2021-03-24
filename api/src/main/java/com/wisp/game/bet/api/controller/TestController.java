package com.wisp.game.bet.api.controller;


import com.wisp.game.bet.api.crud.log.BindYbService;
import com.wisp.game.bet.api.crud.pay.PayService;
import com.wisp.game.bet.api.dao.log.entity.BindYbLogEntity;
import com.wisp.game.bet.api.info.UserInfo;
import com.wisp.core.service.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private BindYbService bindYbService;

    @Autowired
    private PayService payService;


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

        BindYbLogEntity bindYbLogEntity = bindYbService.findLogById(1);

        //return payService.findById(1);

        return bindYbLogEntity;
    }
}
