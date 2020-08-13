package com.wisp.game.bet.logic.sshare;

import com.wisp.game.bet.logic.gameObj.GamePlayer;

public interface IGameEngine {
    public void notify_bot_state(boolean connected);

    //玩家进入游戏
    public boolean player_enter_game(GamePlayer gamePlayer,int roomId);

    public void heartbeat(double elapsed);

    public void exit_engine();

    boolean player_leave_game(int playerid);

    boolean player_leave_game(int playerid, boolean bforce);

    public void zero_time_arrive();

    public int get_gameid();

    //返回一个机器人 返回的机器人未进入房间？
    void response_robot(int playerid, int tag);

    public String get_game_type();

    public int player_join_friend_game( GamePlayer gamePlayer,int friendId );
}
