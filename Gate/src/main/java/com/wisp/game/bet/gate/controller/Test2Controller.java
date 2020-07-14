package com.wisp.game.bet.gate.controller;

import com.wisp.game.db.account.info.AccountTableInfo;
import com.wisp.game.db.account.service.AccountTableServiceImpl;
import com.wisp.game.db.config.info.ServerList;
import com.wisp.game.db.config.service.ServerListImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class Test2Controller {

    @Autowired(required = false)
    private AccountTableServiceImpl accountTableService;

    @Autowired(required = false)
    private ServerListImpl serverListService;

    @RequestMapping("/test")
    public String Index()
    {
        return "i love you more than i can say";
    }



    @RequestMapping("/map")
    public Object IndexMap()
    {
        Map<String,String> map = new ConcurrentHashMap<String, String>();
        map.put("abc","def");

        AccountTableInfo info =  accountTableService.findByAccount("web_225_a811409be73c48eaa014d8767fb8c848");

        List<ServerList> list = serverListService.findAll();

        return list;
    }


}
