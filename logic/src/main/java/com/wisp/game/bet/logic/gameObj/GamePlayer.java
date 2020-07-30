package com.wisp.game.bet.logic.gameObj;

import com.wisp.game.bet.logic.sshare.e_player_state;
import com.wisp.game.bet.logic.unit.LogicPeer;

public class GamePlayer {

    private int m_sessionId;
    private double m_check_kick;
    private e_player_state m_state;


    public int world_id = -1;
    public boolean isRobot = false;
    public int PlayerID = 0;
    public LogicPeer GatePeer;
    public long Gold;
    public String NickName;
    public int Sex;
    public String channelId;
    public int CreateTime;


    public GamePlayer() {
    }

    public void heartbeat( double elapsed )
    {

    }

    public int get_sessionid()
    {
        return m_sessionId;
    }

    public void set_sessionid(int sessionId)
    {
        this.m_sessionId = sessionId;
    }

    public e_player_state get_state()
    {
        return this.m_state;
    }

    public void set_state(e_player_state eps)
    {
        this.m_state = eps;
    }

    public boolean leave_game(boolean shutdown)
    {
        return true;
    }

    public boolean is_robot()
    {
        return isRobot;
    }

    public void set_roomid(int roomId)
    {

    }

    public void reset_gate()
    {

    }

    public void reset_robot_life()
    {

    }

    public int get_playerid()
    {
        return PlayerID;
    }
}
