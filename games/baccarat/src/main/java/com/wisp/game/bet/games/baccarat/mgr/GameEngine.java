package com.wisp.game.bet.games.baccarat.mgr;

import com.wisp.game.bet.games.baccarat.logic.LogicLobby;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.sshare.IGameEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GameEngine implements IGameEngine {
    @Autowired
    private LogicLobby logicLobby;

    @Override
    public void notify_bot_state(boolean connected) {

    }

    @Override
    public boolean player_enter_game(GamePlayer gamePlayer, int roomId) {
        return logicLobby.player_enter_game(gamePlayer,roomId);
    }

    @Override
    public void heartbeat(double elapsed) {
        logicLobby.heartbeat(elapsed);
    }

    @Override
    public void exit_engine() {

    }

    @Override
    public void player_leave_game(int playerid) {
        player_leave_game(playerid,false);
    }

    @Override
    public void player_leave_game(int playerid, boolean bforce) {

    }

    @Override
    public void zero_time_arrive() {
        //do nothing
    }

    @Override
    public int get_gameid() {
        return 5;
    }

    @Override
    public String get_game_type() {
        return "default";
    }

    @Override
    public int player_join_friend_game(GamePlayer gamePlayer, int friendId) {
        int ret =  logicLobby.player_join_friend_game(gamePlayer,friendId);
        if(ret != 1 )
        {
            logicLobby.player_leave_game(gamePlayer.get_playerid());
        }
        return 0;
    }
}
