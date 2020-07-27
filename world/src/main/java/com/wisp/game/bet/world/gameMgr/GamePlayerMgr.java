package com.wisp.game.bet.world.gameMgr;

import com.wisp.game.bet.world.PlayerSys.GamePlayer;
import org.springframework.stereotype.Component;

@Component
public class GamePlayerMgr {
    public static GamePlayerMgr Instance;

    public GamePlayerMgr() {
        Instance = this;
    }


    public int get_player_count() {
        return 0;
    }

    public void leave_game(int gameServerId) {

    }

    public GamePlayer find_player(int sessionId)
    {
        return null;
    }

    public GamePlayer find_player(String account)
    {
        return null;
    }

    public void remove_session(int sessionId)
    {

    }

    public void reset_player(GamePlayer gamePlayer,int sessionId)
    {

    }

}
