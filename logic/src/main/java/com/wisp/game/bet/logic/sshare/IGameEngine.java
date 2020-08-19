package com.wisp.game.bet.logic.sshare;

import com.wisp.game.bet.logic.gameObj.GamePlayer;

public interface IGameEngine {

    public boolean init_engine();

    public void heartbeat(double elapsed);

    public void exit_engine();

    public void notify_bot_state(boolean connected);

    //玩家进入游戏
    public boolean player_enter_game(GamePlayer gamePlayer,int roomId);

    boolean player_leave_game(int playerid);
    boolean player_leave_game(int playerid, boolean bforce);
    boolean player_can_leave(int playerid);

    public int player_join_friend_game( GamePlayer gamePlayer,int friendId );

    void deduct_stock(int room_id, int gold);


    // 时间0点通知
    public void zero_time_arrive();

    public int get_gameid();

    //返回一个机器人 返回的机器人未进入房间？
    void response_robot(int playerid, int tag);

    public String get_game_type();

    //后台通知设置房间
    public void set_room(int agentid, int roomid);
    //通知打开,关闭房间
    public void open_room(int agentid, boolean open);

}
