package com.wisp.game.bet.recharge.controller.test;

import com.wisp.core.interceptor.SkipAuthToken;
import com.wisp.core.service.ResponseResult;
import com.wisp.core.web.base.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@SkipAuthToken
@ResponseResult
public class RechargeController extends BaseController {

    @RequestMapping("/api/playereasyrecharge")
    public Object PlayerEasyRecharge()
    {
        Map<String,Object> map = new HashMap<>();
        double rand = Math.random()*100000;
        map.put("GameOrderId", String.valueOf(rand) );
        return map;
    }
}
