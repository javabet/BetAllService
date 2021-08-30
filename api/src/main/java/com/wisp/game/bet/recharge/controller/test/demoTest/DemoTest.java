package com.wisp.game.bet.recharge.controller.test.demoTest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class DemoTest implements InitializingBean
{
    public void afterPropertiesSet() throws Exception
    {
//        final String publicKey ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDBtcSjaE0gOF9h24sX+tPRN+dNqhuPG916HPyQFtqMxmqqTKWsfr0pNCsjX+8okfSkDereBNSIyec8OTm+RKZwHR9pB0mfEKIif0FceK+ze4oViYplZ+kb1jV0+Oei/lNj7PU/SByRJnNAH1WGvOu10JnViLbUOo4U0/dcV7rVrQIDAQAB";//平台提供
//        final String payPassword = "3F2A5CCD4F954739BD35789E1DFE3A8F";//平台提供
//        final String url = "";//请求下单地址
//        //加密map
//        Map<String, Object> mapParam =   Collections.synchronizedMap(new HashMap<String,Object>());
//        mapParam.put("appid", "100001");//商户账户
//        mapParam.put("ordertime", "20200520120345");//订单时间 yyyyMMddHHmmss
//        mapParam.put("amount", "100");//金额
//        mapParam.put("acctno", "1234567890099");//银行卡
//        mapParam.put("acctname", "张三丰");//开户人
//        mapParam.put("bankcode", "ICBC");//银行代码
//        mapParam.put("bankName", "中国工商银行");//银行名称
//        mapParam.put("notifyurl", "http://127.0.0.1/");//回调地址
//        mapParam.put("apporderid", "111111111111");//订单号
//        mapParam.put("sign", HashKit.md5(MapDataUtil.createParam(mapParam) + payPassword));
//
//        String msg = RSAUtils.getEncryptPublicKey(mapParam, publicKey);
//
//        System.out.println("msg:");
//        System.out.println(msg);

        //请求传参map
//        Map<String, Object> extraParam = new HashMap<String, Object>();
//        extraParam.put("userId","100001");//商户号
//        extraParam.put("cipherText", RSAUtils.getEncryptPublicKey(mapParam, publicKey));
//        String result= HttpUtil.post(url, extraParam);
//        System.out.println(result);
    }
}
