package com.wisp.game.bet.world.gameMgr;

import org.springframework.stereotype.Component;

@Component
public class GameEngineMgr  {
    public static GameEngineMgr Instance;

    public GameEngineMgr() {
        Instance = this;
    }

    public void remove_game_info(int gameId,int serverId)
    {

    }
}
