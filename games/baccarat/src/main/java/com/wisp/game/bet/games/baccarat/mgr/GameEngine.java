package com.wisp.game.bet.games.baccarat.mgr;

import com.wisp.game.bet.GameConfig.GameConfig;
import com.wisp.game.bet.games.baccarat.logic.LogicLobby;
import com.wisp.game.bet.games.baccarat.logic.LogicPlayer;
import com.wisp.game.bet.logic.gameMgr.GameManager;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.sshare.AbstractGameEngine;
import com.wisp.game.bet.logic.sshare.IGameEngine;
import com.wisp.game.bet.logic.sshare.IGamePHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public final class GameEngine extends AbstractGameEngine {

    private static int GAME_ID = 5;

    @Autowired
    private Environment environment;

    @Autowired
    private LogicLobby logicLobby;

    @Autowired
    private GameConfig gameConfig;

    public static GameEngine Instance;

    public GameEngine() {
        Instance = this;
    }

    public boolean init_engine()
    {
        init_db();

        logicLobby.init_game(10000);
        GameManager.Instance.on_init_engine(GAME_ID,"1.0.0");
        return true;
    }


    @Override
    public void heartbeat(double elapsed) {
        logicLobby.heartbeat(elapsed);
    }

    @Override
    public void exit_engine() {
        logicLobby.release_game();
        GameManager.Instance.on_exit_engine();
    }

    @Override
    public boolean player_enter_game(GamePlayer gamePlayer, int roomId) {
        return logicLobby.player_enter_game(gamePlayer,roomId);
    }

    @Override
    public boolean player_leave_game(int playerid) {
        return  player_leave_game(playerid,false);
    }

    @Override
    public boolean player_leave_game(int playerid, boolean bforce) {
        return true;
    }

    @Override
    public boolean player_can_leave(int playerid) {
        return true;
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

    @Override
    public void set_room( int agentId,int roomId )
    {
        logicLobby.set_room(agentId,roomId);
    }

    @Override
    public void open_room(int agentid, boolean open)
    {
        logicLobby.open_room(agentid, open);
    }


    private void init_db()
    {
        //do nothing
    }

    @Override
    public void response_robot(int playerid, int tag) {
        logicLobby.response_robot(playerid,tag);
    }

    //请求一个机器人
    public void request_robot(int tag, boolean banker, boolean exroom)
    {
        GameManager.Instance.request_robot(tag, banker, 0, exroom);
    }


    public void deduct_stock(int room_id, int gold)
    {
        logicLobby.deduct_stock(room_id, gold);
    }

    @Override
    public void notify_bot_state(boolean connected) {
        //do nothing
    }

    public void release_robot(int playerid)
    {
        GameManager.Instance.release_robot(playerid);
    }

    @Override
    public void zero_time_arrive() {
        //do nothing
    }

    @Override
    public int get_gameid() {
        return GAME_ID;
    }



    @Override
    public String get_game_type() {
        return "default";
    }
}
