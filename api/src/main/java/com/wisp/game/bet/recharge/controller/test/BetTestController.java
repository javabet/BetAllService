package com.wisp.game.bet.recharge.controller.test;

import com.google.gson.Gson;
import com.wisp.core.interceptor.SkipAuthToken;
import com.wisp.core.service.ResponseResult;
import com.wisp.core.utils.APIUtils;
import com.wisp.core.vo.ResponseResultVo;
import com.wisp.core.web.base.BaseController;
import com.wisp.game.bet.recharge.common.ErrorCode;
import com.wisp.game.bet.recharge.controller.test.info.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@ResponseResult
@Validated
@SkipAuthToken
public class BetTestController extends BaseController
{
    @PostMapping(value = "/playerpayment")
    public Object playerpayment(@RequestParam("PayType") int PayType,@RequestParam(value = "AccountName",required = false) String  accountName )
    {
        long expire_tm = new Date().getTime()/1000l + 1800;
        if( PayType == 12 )
        {
            BankToBankInfo bankToBankInfo = new BankToBankInfo();
            bankToBankInfo.setReceiveAmount(10000);
            bankToBankInfo.setReceiveBank("bankName");
            bankToBankInfo.setReceiveSubbank("subBankName");
            bankToBankInfo.setReceiveBankAccount("bankAccount");
            bankToBankInfo.setReceiveRemark("remark"); 
            bankToBankInfo.setReceiveExtend("extends");
            bankToBankInfo.setReceiveName("receiveName");
            bankToBankInfo.setReturnParam(accountName);
            bankToBankInfo.setReceiveExpireTime(Integer.valueOf(String.valueOf(expire_tm)));

            PlayerPayUrlInfo playerPayUrlInfo = new PlayerPayUrlInfo();
            playerPayUrlInfo.setType(4);
            playerPayUrlInfo.setCard2CardReceiveInfo(new Gson().toJson(bankToBankInfo));
            //playerPayUrlInfo.setCard2CardReceiveInfo("");
            playerPayUrlInfo.setData("");

            PlayerPaymentInfo playerPaymentInfo = new PlayerPaymentInfo();
            playerPaymentInfo.setOrderId(APIUtils.getUUID());
            playerPaymentInfo.setOwnWebBrowser(false);
            playerPaymentInfo.setPayUrl( new Gson().toJson(playerPayUrlInfo));

            return  playerPaymentInfo;
        }
        else if( PayType == 13 )
        {
            UsdtInfo usdtInfo = new UsdtInfo();
            usdtInfo.setRealBuyNum(93);
            usdtInfo.setTransactionId("000fefefae00");
            usdtInfo.setBuyNum(95);
            usdtInfo.setErc20Address("0x64561348cd417751B0A2d63Ab64118FFCE93C1ef");
            usdtInfo.setErc20QrCode("http://www.ezf-erc20.com/upload/2021/07/06/7ebe34197e1d5e1e83703664354a1998.jpg");
            usdtInfo.setTrc20Address("TMk6YRQfmwMrBNqqYXEAVhvm41qe799bQY");
            usdtInfo.setTrc20QrCode("http://www.ezf-erc20.com/upload/2021/06/19/37977c9e3600771d555e9f5589a32602.jpg");
            usdtInfo.setSinglePrice(10);
            long cur_time = new Date().getTime()/1000;
            double cur_time_d =  Math.floor( (double)cur_time);
            usdtInfo.setCreateTime( (int)cur_time_d );
            usdtInfo.setReturnParam(accountName);
            usdtInfo.setReceiveExpireTime(Integer.valueOf(String.valueOf(expire_tm)));


            PlayerPayUrlInfo playerPayUrlInfo = new PlayerPayUrlInfo();
            playerPayUrlInfo.setType(4);
            playerPayUrlInfo.setCard2CardReceiveInfo(new Gson().toJson(usdtInfo));
            playerPayUrlInfo.setData("");

            PlayerPaymentInfo playerPaymentInfo = new PlayerPaymentInfo();
            playerPaymentInfo.setOrderId(APIUtils.getUUID());
            playerPaymentInfo.setOwnWebBrowser(false);
            playerPaymentInfo.setPayUrl( new Gson().toJson(playerPayUrlInfo));

            return  playerPaymentInfo;
        }
        else
        {
            NormalRechargeInfo normalRechargeInfo = new NormalRechargeInfo();
            normalRechargeInfo.setType(1);
            normalRechargeInfo.setData("https://wwww.baidu.com");

            PlayerPaymentInfo playerPaymentInfo = new PlayerPaymentInfo();
            playerPaymentInfo.setOrderId(APIUtils.getUUID());
            playerPaymentInfo.setOwnWebBrowser(false);
            playerPaymentInfo.setPayUrl( new Gson().toJson(normalRechargeInfo));
            return playerPaymentInfo;
        }

        //return emptySucc();
    }

    /**
    @PostMapping(value = "/playerwithdraw")
    public Object playerwithdraw(@RequestParam("PlayerId") int PlayerId,@RequestParam("GameOrderId") String GameOrderId,@RequestParam("ChannelId") String ChannelId,
                                 @RequestParam("AccountType") int AccountType,@RequestParam("Amount") int Amount,
                                 @RequestParam("DigitalType") String DigitalType,@RequestParam("DigitalNum") int DigitalNum,@RequestParam("DigitalAddress")String DigitalAddress )
    {


        System.out.println("AccountType:" + AccountType + " Amount:" + Amount);

        return data("success");
    }
    **/

    @RequestMapping(value = "/playerwithdraw")
    public Object playerwithdraw( )
    {


        System.out.println("playerWithdraw");

        return data("success");
    }

    @RequestMapping(value="/usdtrate")
    public Object usdtrate()
    {

        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> usdt1 = new HashMap<>();
        usdt1.put("Type",1);
        usdt1.put("Desc","TRC20");
        usdt1.put("Fee","1");
        usdt1.put("IsOpen",1);
        list.add(usdt1);

        Map<String,Object> usdt2 = new HashMap<>();
        usdt2.put("Type",2);
        usdt2.put("Desc","ERC20");
        usdt2.put("Fee","0");
        usdt2.put("IsOpen",1);
        list.add(usdt2);

        Map<String,Object> map = new HashMap<>();
        map.put("UsdtRate",648);
        map.put("UsdtTrasFeeList",list);

        return map;
    }

}
