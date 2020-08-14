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

    public GameEngineMgr() {
        Instance = this;
        m_games = new HashMap<>();
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

    public boolean add_game_info( int gameId,int gamever,int serverId )
    {
        GameInfo gameInfo =  m_games.get(gameId);
        if( gameInfo != null )
        {
            if( gameInfo.getServersMap().get(serverId) == null )
            {
                gameInfo.getServersMap().put(serverId,0);
            }
            return true;
        }
        return false;
    }

    public GameInfo find_game_info( int serverid )
    {
        return m_games.get(serverid);
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

    //查找玩家最少的那个服务器
    public GameInfoStruct get_game_info_struct(int gameId)
    {
        if( !m_games.containsKey(gameId) )
        {
            return null;
        }

        int server_id = -1;
        int player_cnt = Integer.MAX_VALUE;

        Map<Integer,Integer> serversMap = m_games.get(gameId).getServersMap();

        for(Integer key : serversMap.keySet())
        {
            if( serversMap.get(key) >= player_cnt )
            {
                continue;
            }

            player_cnt = serversMap.get(key);
            server_id = key;
        }

        GameInfoStruct gameInfoStruct = new GameInfoStruct();
        gameInfoStruct.gameId = gameId;
        gameInfoStruct.serverId = server_id;
        gameInfoStruct.gamever = m_games.get(gameId).getGameVer();


        return gameInfoStruct;
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

    public GameInfo get_game_info(int gameId)
    {
        return m_games.get(gameId);
    }

    public class GameInfoStruct
    {
        public  int gameId;
        public int serverId;
        public int gamever;
    }

}
