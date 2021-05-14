package com.wisp.game.bet.api.controller;

import com.wisp.core.service.ResponseResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ResponseResult
@RestController
public class PlayerCardController
{
    //@RequestMapping("/playercard");
    @RequestMapping("/playercard")
    public Object bindCard()
    {
        return "success";
    }
}
