package com.wisp.game.bet.http.controller;


import com.wisp.game.bet.http.controller.info.ReqBalanceDto;
import com.wisp.game.core.web.base.BaseController;
import com.wisp.game.core.web.response.SuccessRespBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/Api/Single")
@RestController
public class SingleApiController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(getClass());


    @RequestMapping("/Balance/CheckThirdBalance")
    public Object CheckBalance2(@RequestParam("AgentId") int AgentId,@RequestParam("ChannelId") String chanelId,@RequestParam("PlayerId") int playerId,@RequestParam("UserName") String userName )
    {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("Balance",1001);
        SuccessRespBean<Map<String,Object>> successRespBean = new SuccessRespBean<Map<String, Object>>();
        successRespBean.setCode(0);
        successRespBean.setData(map);
        return successRespBean;
    }


    //@RequestMapping("/Balance/CheckThirdBalance")
    public Object CheckBalance(@RequestBody ReqBalanceDto reqDto )
    {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("Balance",1001);
        SuccessRespBean<Map<String,Object>> successRespBean = new SuccessRespBean<Map<String, Object>>();
        successRespBean.setCode(10070);
        successRespBean.setData(map);
        return successRespBean;
    }


    @RequestMapping("/Balance/AddBalance")
    public Object AddBalance(@RequestBody ReqBalanceDto reqDto)
    {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("Balance",reqDto.getBalance());
        SuccessRespBean<Map<String,Object>> successRespBean = new SuccessRespBean<Map<String, Object>>();
        successRespBean.setCode(200);
        successRespBean.setData(map);
        return successRespBean;
    }

    @RequestMapping("/Player/LogOut")
    public Object LogOut( @RequestBody ReqBalanceDto reqDto )
    {
        logger.info("player's gold:" + reqDto.getBalance());
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("OrderId",reqDto.getOrderId());
        SuccessRespBean<Map<String,Object>> successRespBean = new SuccessRespBean<Map<String, Object>>();
        successRespBean.setCode(200);
        successRespBean.setData(map);
        return successRespBean;
    }

}
