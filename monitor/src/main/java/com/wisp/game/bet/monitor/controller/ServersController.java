package com.wisp.game.bet.monitor.controller;

import com.wisp.game.bet.monitor.unit.ServerManager;
import com.wisp.game.core.web.response.SuccessRespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server_protocols.ServerProtocol;

@RestController
public class ServersController {

    @Autowired
    private ServerManager serverManager;

    @RequestMapping(method = {RequestMethod.POST},path = "/shutdown")
    public Object shutdown(@RequestParam(name = "gameid") int gameId)
    {
        ServerProtocol.packet_shutdown.Builder builder = ServerProtocol.packet_shutdown.newBuilder();
        builder.setGameid(gameId);
        serverManager.broadcast_msg(builder);


        SuccessRespBean<String> respBean = new SuccessRespBean<>();
        respBean.setCode(0);
        respBean.setMessage("ok");
        respBean.setData("success");

        return respBean;
    }
}
