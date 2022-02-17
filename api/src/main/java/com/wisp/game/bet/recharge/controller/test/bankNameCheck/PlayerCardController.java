package com.wisp.game.bet.recharge.controller.test.bankNameCheck;

import com.qiniu.http.Response;
import com.wisp.core.interceptor.SkipAuthToken;
import com.wisp.core.service.ResponseResult;
import com.wisp.core.web.base.BaseController;
import com.wisp.game.bet.recharge.controller.test.bankNameCheck.info.DigitalAddressInfo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@ResponseResult
@RestController
@SkipAuthToken
public class PlayerCardController extends BaseController
{
    //@RequestMapping("/playercard");
    @RequestMapping("/api/playercard")
    @SkipAuthToken()
    public Object bindCard(@RequestParam(value = "OperationType",required = false,defaultValue = "1") int OperationType)
    {
        /**
        Map<String,Object> map = new HashMap<>();
        if( OperationType == 1 )
        {

            StringBuilder sb = new StringBuilder();
            sb.append("rand").append(Math.random()*10000);
            map.put("PlatformId", sb.toString() );
        }
        else
        {

            StringBuilder sb = new StringBuilder();
            sb.append("rand").append(Math.random()*10000);
            map.put("PlatformId", sb.toString() );
        }

        return data(map);
         **/

        return "success";
    }

    @RequestMapping("/api/withdrawdigitaladdress")
    public Object digital_address(@RequestParam("OperationType") int OperationType)
    {
        if( OperationType== 1 )
        {
            Map<String,Object> map = new HashMap<>();
            StringBuilder sb = new StringBuilder();
            sb.append("rand").append(Math.random()*10000);
            map.put("DigitalId", sb.toString() );
            return data(map);
        }
        else if( OperationType == 2 )
        {
            return data("success");
        }

        return data("success1");
    }
}
