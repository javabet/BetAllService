package com.wisp.game.bet.games.baccarat.logic;

import com.wisp.game.bet.logic.gameObj.GamePlayer;
import org.springframework.stereotype.Component;

@Component
public class LogicLobby {

    public boolean player_enter_game(GamePlayer gamePlayer,int roomId)
    {
        return true;
    }

    public void heartbeat( double elapsed )
    {

    }

    public int player_join_friend_game(GamePlayer gamePlayer,int friendId)
    {
        return 1;
    }

    public void player_leave_game(int playerId)
    {

    }
}
