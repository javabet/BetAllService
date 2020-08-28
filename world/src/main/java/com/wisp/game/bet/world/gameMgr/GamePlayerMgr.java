package com.wisp.game.bet.world.gameMgr;

import client2world_protocols.Client2WorldProtocol;
import com.wisp.game.bet.db.mongo.player.doc.CommonConfigDoc;
import com.wisp.game.bet.db.mongo.player.doc.OrderPlayerIdDoc;
import com.wisp.game.bet.share.component.TimeHelper;
import com.wisp.game.bet.world.PlayerSys.GamePlayer;
import com.wisp.game.bet.world.db.DbPlayer;
import com.wisp.game.bet.world.unit.ServersManager;
import com.wisp.game.bet.world.unit.WorldPeer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import server_protocols.ServerProtocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GamePlayerMgr {
    private Logger logger = LoggerFactory.getLogger(getClass());
    public static GamePlayerMgr Instance;

    private boolean b_closing;
    private List<Integer> m_kickList;
    private int m_lastTime;
    private Map<Integer, Integer> m_onlineNum;       //各个游戏的在线人数, 游戏ID->人数
    private double m_checktime = 0;
    private Map<Integer, GamePlayer> m_playerById;
    private Map<Integer, GamePlayer> m_playersbysid;
    private Map<String, GamePlayer> m_playersbyacc;
    private List<GamePlayer> m_dellist;

    private double m_recheck_kick = 0;

    public GamePlayerMgr() {
        Instance = this;
        m_kickList = new ArrayList<>();
        m_onlineNum = new ConcurrentHashMap<>();
        m_playerById = new ConcurrentHashMap<>();
        m_playersbysid = new ConcurrentHashMap<>();
        m_playersbyacc = new ConcurrentHashMap<>();
        m_dellist = new ArrayList<>();
    }

    public GamePlayer find_player(String account) {
        return m_playersbyacc.get(account);
    }

    public GamePlayer find_player(int sessionId) {
        return m_playersbysid.get(sessionId);
    }

    public GamePlayer findPlayerById(int playerId) {
        return m_playerById.get(playerId);
    }

    public boolean add_player(GamePlayer gamePlayer) {
        if (m_playersbysid.containsKey(gamePlayer.get_sessionid())) {
            return false;
        }
        m_playersbysid.put(gamePlayer.get_sessionid(), gamePlayer);
        m_playersbyacc.put(gamePlayer.getAccountTableInfoDoc().getAccount(), gamePlayer);

        return true;
    }

    public boolean addPlayerById(GamePlayer gamePlayer) {
        if (!m_playerById.containsKey(gamePlayer.getPlayerInfoDoc().getPlayerId())) {
            m_playerById.put(gamePlayer.getPlayerInfoDoc().getPlayerId(), gamePlayer);
        }

        return true;
    }

    public void remove_player(GamePlayer gamePlayer) {
        m_playersbysid.remove(gamePlayer.get_sessionid());
        m_playerById.remove(gamePlayer.getPlayerInfoDoc().getPlayerId());
        m_playersbyacc.remove(gamePlayer.getAccountTableInfoDoc().getAccount());
    }

    public void reset_player(GamePlayer gamePlayer, int sessionId) {
        m_playersbysid.remove(gamePlayer.get_sessionid());
        gamePlayer.set_sessionid(sessionId);
        m_playersbysid.put(sessionId, gamePlayer);
    }

    public void remove_session(int sessionId) {
        m_playersbysid.remove(sessionId);
    }

    public void heartbeat(double elapsed) {
        for (GamePlayer gamePlayer : m_playersbyacc.values()) {
            gamePlayer.heartbeat(elapsed);
        }

        //被踢除的人
        m_recheck_kick += elapsed;
        if (m_recheck_kick >= 3000) {
            m_recheck_kick = 0;

            if (!m_kickList.isEmpty()) {
                for (int playerId : m_kickList) {
                    GamePlayer gamePlayer = findPlayerById(playerId);
                    if (gamePlayer != null) {
                        gamePlayer.player_logout();
                        remove_session(gamePlayer.get_sessionid());
                    }
                }

                m_kickList.clear();
            }
        }

        //移除的人
        if (m_dellist.size() > 0) {
            for (GamePlayer gamePlayer : m_dellist) {
                remove_player(gamePlayer);
            }
            m_dellist.clear();
        }

        if (ServersManager.Instance.is_shutdowning()) {
            shutdown_heartbeat(elapsed);
        }
    }

    public void set_del_player(GamePlayer gamePlayer) {
        m_dellist.add(gamePlayer);
    }


    public int generic_playerid() {
        Query query = new Query(Criteria.where("type").is("cur_index"));
        Update update = new Update();
        update.inc("value", 1);
        CommonConfigDoc commonConfigDoc = DbPlayer.Instance.getMongoTemplate().findAndModify(query, update, CommonConfigDoc.class);
        if (commonConfigDoc == null) {
            return 0;
        }

        OrderPlayerIdDoc orderlayerIdDoc = DbPlayer.Instance.getMongoTemplate().findOne(new Query(Criteria.where("Index").is(commonConfigDoc.getValue())), OrderPlayerIdDoc.class);
        if (orderlayerIdDoc == null) {
            return 0;
        }

        return orderlayerIdDoc.getPid();
    }

    public void leave_game( int gameserverId )
    {
        int gameId = 0;

       for( GamePlayer gamePlayer : m_playerById.values() )
        {
            if( gamePlayer.get_logicid() != gameserverId )
            {
                continue;
            }

            if( gameId == 0 )
            {
                gameId = gamePlayer.get_gameid();
            }

            gamePlayer.leave_game();

            if( gamePlayer.getPlayerInfoDoc().isRobot() )
            {
                gamePlayer.player_logout();
            }
        }

        if( gameId > 0 )
        {
            onClearGame(gameId);
        }
    }

    public void onEnterGame( int gameId )
    {
        if( !m_onlineNum.containsKey(gameId) )
        {
            m_onlineNum.put(gameId,0);
        }

        int oldValue =m_onlineNum.get(gameId);
        m_onlineNum.put(gameId, oldValue + 1);
    }

    public void onExitGame(int gameId)
    {
        int oldValue =m_onlineNum.get(gameId);
        if( oldValue > 0 )
        {
            m_onlineNum.put(gameId, oldValue - 1);
        }
    }

    public void onClearGame(int gameId)
    {
        if( m_onlineNum.get(gameId) > 0 )
        {
            m_onlineNum.put(gameId,0);
        }
    }


    public int getOnlineNumInGame( int gameId )
    {
        return m_onlineNum.get(gameId);
    }

    public void shutdown_heartbeat( double elapsed )
    {
        if( m_playerById.size() == 0 )
        {
            ServersManager.Instance.serverdown();
            return;
        }


        for( GamePlayer gamePlayer : m_playerById.values() )
        {
            if( gamePlayer.get_gameid() == 0 )
            {
                WorldPeer gatePeer =  gamePlayer.get_gate();
                if(  gatePeer!= null )
                {
                    ServerProtocol.packet_player_disconnect.Builder builder = ServerProtocol.packet_player_disconnect.newBuilder();
                    builder.setSessionid(gamePlayer.get_sessionid());
                    builder.setShutdown(true);
                    gatePeer.send_msg(builder.build());
                    logger.info("shutdown_heartbeat ServerProtocol.packet_player_disconnect333.Builder");
                }
                GamePlayerMgr.Instance.set_del_player(gamePlayer);
            }
        }
    }


    public int get_player_count() {
        return m_playersbyacc.size();
    }


    public void block_player( int playerId,int block_time )
    {

    }

    public void kick_player( int playerId,int kick_end_time )
    {

    }

    public void kick_all_player( int agent_id )
    {
        if( agent_id <= 0 )
        {
            return;
        }

        int cur_tm_s = TimeHelper.Instance.get_cur_time() + 600;
        for( GamePlayer gamePlayer : m_playerById.values() )
        {
            if( gamePlayer.getPlayerInfoDoc().getAgentId() != agent_id )
            {
                continue;
            }

            Client2WorldProtocol.packetw2c_player_kick.Builder builder = Client2WorldProtocol.packetw2c_player_kick.newBuilder();
            builder.setKickType(1);
            gamePlayer.send_msg_to_client(builder.build());
            //gamePlayer.getPlayerInfo().setKickEndTime(cur_tm_s * 1000);
            m_kickList.add( gamePlayer.getPlayerInfoDoc().getPlayerId() );
        }
    }



}
