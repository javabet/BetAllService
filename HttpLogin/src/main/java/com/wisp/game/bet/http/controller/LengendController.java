package com.wisp.game.bet.http.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/Api/Lengend")
public class LengendController {

    @RequestMapping("/GetBalance")
    public Object GetBalance(@RequestParam("username") String userName,@RequestParam("product") String product,@RequestParam("agentcode") String agentcode,
                             @RequestParam("webcode") String webcode,@RequestParam("sign") String sign)
    {

        System.out.println("userName:" + userName);

        return new HashMap();
    }
}
