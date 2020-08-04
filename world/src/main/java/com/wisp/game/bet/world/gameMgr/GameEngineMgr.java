package com.wisp.game.bet.world.gameMgr;

import com.wisp.game.bet.GameConfig.MainGameVerConfig;
import com.wisp.game.bet.world.db.DbPlayer;
import com.wisp.game.bet.world.gameMgr.info.AgentRooms;
import com.wisp.game.bet.world.gameMgr.info.GameInfo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class GameEngineMgr implements InitializingBean {
    public static GameEngineMgr Instance;
    private double m_elapsed = 0;
    private Map<Integer,GameInfo> m_games;
    private Map<Integer,Map<Integer, AgentRooms>> m_agent_rooms;

    public GameEngineMgr() {
        Instance = this;
        m_games = new HashMap<>();
        m_agent_rooms = new HashMap<>();
    }

    public void afterPropertiesSet() throws Exception
    {
        init_games();
    }
    
    public void heartbeat(double elapsed )
    {
        m_elapsed -= elapsed;

        if( m_elapsed > 0 )
        {
            return;
        }


        m_elapsed = 60 * 1000;
    }

    private void init_games()
    {
        MainGameVerConfig.GetInstnace().Reload();
        Map<Integer,MainGameVerConfig.MainGameVerConfigData> datas = MainGameVerConfig.GetInstnace().GetMapData();
        for( MainGameVerConfig.MainGameVerConfigData configData : datas.values() )
        {
            if( configData.getmIsOpen() == false )
            {
                continue;
            }

            GameInfo gameInfo = new GameInfo();
            gameInfo.setGameId(configData.getmID());
            gameInfo.setGameVer(configData.getmGameVer());
            gameInfo.setMinVer(configData.getmMinVer());
            gameInfo.setH5GameVer( configData.getmH5GameVer() );
            gameInfo.setRoomcard_config(null);
            m_games.put(gameInfo.getGameId(),gameInfo);
        }
    }

    public Map<Integer,GameInfo> get_gamelist()
    {
        return m_games;
    }

    public void update_server_info( int gameId,int serverId,int player_cnt )
    {
        if( !m_games.containsKey(gameId) )
        {
            return;
        }

        m_games.get(gameId).getServersMap().put(serverId,player_cnt);
    }

    public void remove_game_info(int gameId,int serverId)
    {
       if( !m_games.containsKey(gameId) )
       {
           return;
       }

       m_games.get(gameId).getServersMap().remove(serverId);

    }

    public void update_game_info(int gameId,int serverId )
    {
        this.update_game_info(gameId,serverId,true);
    }


    public int update_game_info(int gameId,int serverId,boolean add_player )
    {
        if( m_games.containsKey(gameId) )
        {
            return -1;
        }

        if( !m_games.get(gameId).getServersMap().containsKey(serverId) )
        {
            return  -1;
        }

        int player_cnt = m_games.get(gameId).getServersMap().get(serverId);
        player_cnt += add_player ? 1 : -1;
        m_games.get(gameId).getServersMap().put(serverId,player_cnt);

        return player_cnt;
    }

    public GameInfo get_game_info(int gameId)
    {
        return m_games.get(gameId);
    }

    public int get_game_info_player_cnt(int gameId,int gameVer )
    {
        if( !m_games.containsKey(gameId) )
        {
            return 0;
        }

        GameInfo gameInfo = m_games.get(gameId);

        int serverId = 0;
        int player_cnt = Integer.MAX_VALUE;
        for(int key : gameInfo.getServersMap().keySet())
        {
            if( gameInfo.getServersMap().get(key) < player_cnt )
            {
                player_cnt = gameInfo.getServersMap().get(key);
                serverId = key;
            }
        }

        return serverId;
    }

}
