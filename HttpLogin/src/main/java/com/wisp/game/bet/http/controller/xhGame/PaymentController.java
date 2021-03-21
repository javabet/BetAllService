package com.wisp.game.bet.http.controller.xhGame;

import com.wisp.game.bet.db.mongo.player.doc.PlayerInfoDoc;
import com.wisp.game.bet.http.controller.info.ReqBalanceDto;
import com.wisp.game.bet.http.controller.xhGame.info.InnerResponse;
import com.wisp.game.bet.http.controller.xhGame.info.PlayermentInfoResponse;
import com.wisp.game.bet.http.controller.xhGame.info.PlayerpaymentInfo;
import com.wisp.game.bet.http.controller.xhGame.info.XhResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PaymentController
{
    private Logger logger = LoggerFactory.getLogger("PaymentController");

    @RequestMapping(value = "/playerpayment",method = RequestMethod.POST)
    public Object GetBalance(@RequestParam Map<String, Object> playerpaymentInfo)
    {
        XhResponse<PlayermentInfoResponse> sendResponse = new XhResponse<PlayermentInfoResponse>();
        sendResponse.setCode(200);

        InnerResponse<PlayermentInfoResponse> innerResponse = new InnerResponse();

        PlayermentInfoResponse response = new PlayermentInfoResponse();
        response.setPayUrl("http://www.baidu.com");
        response.setOrderId("1111111");
        innerResponse.setData(response);
        innerResponse.setCode(200);
        sendResponse.setMsg(innerResponse);

        return sendResponse;
    }
}
