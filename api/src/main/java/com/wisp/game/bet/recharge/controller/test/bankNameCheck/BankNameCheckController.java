package com.wisp.game.bet.recharge.controller.test.bankNameCheck;

import com.wisp.core.interceptor.SkipAuthToken;
import com.wisp.core.service.ResponseResult;
import com.wisp.core.web.base.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Validated
@SkipAuthToken
public class BankNameCheckController extends BaseController {

    @GetMapping(value = "/shuliancloud/bankcard2_check")
    public Object bankcard2_check()
    {
        Map map = new HashMap();
        map.put("code","10000");
        Map resultMap = new HashMap();
        Map dataMap = new HashMap();
        dataMap.put("result","success");
        resultMap.put("data",dataMap);
        map.put("result",resultMap);
        return map;
    }


    @RequestMapping(value = "/v4/bankcard2/check")
    public Object bankcard1_check()
    {
        Map map = new HashMap();
        map.put("code",200);
        map.put("success",true);
        Map dataMap = new HashMap();
        dataMap.put("result",0);
        map.put("data",dataMap);
        return map;
    }
}
