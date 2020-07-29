package com.wisp.game.bet.logic.gameMgr;

import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.unit.LogicServer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GamePlayerMgr {
    public static GamePlayerMgr Instance;

    private double m_closeing_time = 0;
    private double m_checktime = 0;
    private List<Integer> m_playerdels;
    private Map<Integer, GamePlayer> m_playersBypId;
    private Map<Integer,GamePlayer> m_playersBySid;

    public GamePlayerMgr() {
        Instance = this;

        m_playerdels = new ArrayList<>();
        m_playersBypId = new ConcurrentHashMap<>();
        m_playersBySid = new ConcurrentHashMap<>();
    }

    public Map<Integer,GamePlayer> get_player_map()
    {
        return m_playersBySid;
    }

    public GamePlayer find_playerbyid(int playerid)
    {
        return m_playersBypId.get(playerid);
    }

    public GamePlayer find_player(int sessionid)
    {
        return m_playersBySid.get(sessionid);
    }

    public boolean add_player( GamePlayer gamePlayer )
    {
        if( m_playersBypId.containsKey(gamePlayer.PlayerID) )
        {
            return false;
        }

        m_playersBypId.put(gamePlayer.PlayerID,gamePlayer);
        m_playersBySid.put(gamePlayer.get_sessionid(),gamePlayer);
        return true;
    }

    public void remove_player(GamePlayer gamePlayer)
    {
        m_playersBypId.remove(gamePlayer.PlayerID);
        m_playersBySid.remove(gamePlayer.get_sessionid());
    }

    public void reset_player(GamePlayer p,int sessionId)
    {
        if( sessionId <= 0 )
        {
            return;
        }

        m_playersBySid.remove(p.get_sessionid());
        p.set_sessionid(sessionId);
        m_playersBySid.put( p.get_sessionid(),p );
    }

    public void remove_session(int sessionId)
    {
        m_playersBySid.remove(sessionId);
    }

    public void heartbeat( double elapsed )
    {
        for( int playerId : m_playerdels )
        {
            GamePlayer gamePlayer = find_playerbyid(playerId);
            if( gamePlayer != null )
            {
                if( gamePlayer.leave_game(false) )
                {
                    remove_player(gamePlayer);
                }
            }
        }

        m_playerdels.clear();

        for( GamePlayer gamePlayer : m_playersBypId.values() )
        {
            gamePlayer.heartbeat(elapsed);
        }

        if( m_closeing_time > 0 )
        {
            m_closeing_time -= elapsed;
            if( m_closeing_time <  0)
            {
                LogicServer.Instance.close();
            }
        }
        else
        {
            if( GameManager.Instance.is_shutdowning() )
            {
                //玩家都离开了，可以立刻关服。
                if( m_playersBypId.size() == 0 )
                {
                    GameManager.Instance.serverdown();
                }
            }
        }
    }

    public void shutdown()
    {
        for( GamePlayer gamePlayer : m_playersBypId.values())
        {
            gamePlayer.leave_game(true);
        }
        m_playersBypId.clear();;
        m_playerdels.clear();
        m_playersBySid.clear();
    }

    public boolean is_closing()
    {
        return m_closeing_time > 0;
    }

    public void del_player(int pid)
    {
        m_playerdels.add(pid);
    }



    public int get_player_count()
    {
        return m_playersBypId.size();
    }

    public void world_server_down( int server_id )
    {
        for( GamePlayer gamePlayer : m_playersBypId.values() )
        {
            if( !gamePlayer.is_robot() && gamePlayer.world_id == server_id )
            {
                gamePlayer.leave_game(true);
            }
        }
    }


}
