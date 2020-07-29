package com.wisp.game.bet.logic.gameMgr;

import org.springframework.stereotype.Component;

@Component
public class RobotManager {
    public static RobotManager Instance;

    public RobotManager() {
        Instance = this;
    }

    public void set_gameid(int gameId)
    {

    }
}
