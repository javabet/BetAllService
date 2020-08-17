package com.wisp.game.bet.world.controller;

import com.wisp.game.bet.world.gameMgr.GameEngineMgr;
import com.wisp.game.bet.world.gameMgr.GameRoomMgr;
import com.wisp.game.core.web.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/world/test/agent")
public class TestAgentGameController extends BaseController {

    @Autowired
    private GameEngineMgr gameEngineMgr;

    @Autowired
    private GameRoomMgr gameRoomMgr;

    @RequestMapping(value = "/init_game",method = {RequestMethod.POST,RequestMethod.GET})
    public Object InitAgentGame(@RequestParam(name = "agent") int agentId)
    {
        gameRoomMgr.init_room( agentId );

        return data(new HashMap<>());
    }
}
