package com.wisp.game.bet.world.PlayerSys;

import client2world_protocols.Client2WorldProtocol;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.wisp.game.bet.db.mongo.account.doc.AccountTableDoc;
import com.wisp.game.bet.db.mongo.player.doc.PlayerInfoDoc;
import com.wisp.game.bet.share.component.TimeHelper;
import com.wisp.game.bet.share.utils.ProtocolClassUtils;
import com.wisp.game.bet.share.utils.SessionHelper;
import com.wisp.game.bet.sshare.convert.TimeInt;
import com.wisp.game.bet.world.db.DbPlayer;
import com.wisp.game.bet.world.gameMgr.GameEngineMgr;
import com.wisp.game.bet.world.gameMgr.GamePlayerMgr;
import com.wisp.game.bet.world.gameMgr.GameRoomMgr;
import com.wisp.game.bet.world.unit.ServersManager;
import com.wisp.game.bet.world.unit.WorldPeer;
import logic2world_protocols.Logic2WorldProtocol;
import msg_type_def.MsgTypeDef;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import server_protocols.ServerProtocol;

import java.util.Date;

public class GamePlayer {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private boolean  m_login;
    private int m_check_outline = 0;
    private double m_checkip_time;
    private int m_cur_tm;
    private int m_sessionid;
    private int m_logicid;
    private int m_gameid;
    private int m_roomId;
    private e_player_state m_state;

    private int m_check_save;
    private boolean is_relogin;

    private WorldPeer m_gatePeer;
    private WorldPeer m_logicPeer;

    private PlayerInfoDoc playerInfoDoc;
    private AccountTableDoc accountTableInfoDoc;

    public GamePlayer() {
        this.playerInfoDoc = new PlayerInfoDoc();
    }

    public void heartbeat( double elapsed )
    {
        if( m_state == e_player_state.e_ps_disconnect )
        {
            m_check_outline += elapsed;
            if( m_logicid == 0 && m_gameid == 0 )
            {
                if( m_check_outline > 400 * 1000 ) //400s 不在线，则踢下线
                {
                    GamePlayerMgr.Instance.set_del_player(this);
                    m_check_outline = 0;
                }
            }
        }
        else
        {
            m_check_save += elapsed;
            if( m_check_save > 1000 )       //1秒钟 即需要将数据写入数据库中
            {
                store_game_object();
                m_check_save = 0;
            }
        }
    }

    private void on_logout()
    {
        if( m_gatePeer != null )
        {
            ServerProtocol.packet_clear_session.Builder builder = ServerProtocol.packet_clear_session.newBuilder();
            builder.setSessionid(m_sessionid);
            m_gatePeer.send_msg(builder.build());
        }


        store_game_object();
    }

    public void player_logout()
    {
        m_state = e_player_state.e_ps_disconnect;
        m_check_outline = 0;
        if(m_logicPeer != null )
        {
            ServerProtocol.packet_player_disconnect.Builder builder = ServerProtocol.packet_player_disconnect.newBuilder();
            builder.setSessionid(m_sessionid);
            m_logicPeer.send_msg(builder.build());
            logger.info("ServerProtocol.packet_player_disconnect.11Builder player_logout ");
        }
        else
        {
            GamePlayerMgr.Instance.set_del_player(this);
        }
    }


    public void player_login(AccountTableDoc accountTableInfo, String platfrom, String loginPlatform, boolean isRelogin, String csToken)
    {
        Query query = new Query(Criteria.where("Account").is(accountTableInfo.getAccount()));

        Document doc = DbPlayer.Instance.getMongoTemplate().findOne(query,Document.class,DbPlayer.DB_PLAYER_INFO);
        this.playerInfoDoc = DbPlayer.Instance.getMongoTemplate().findOne(query, PlayerInfoDoc.class);
        this.accountTableInfoDoc = accountTableInfo;

        reset_gatepeer();

        if( !is_relogin )
        {
            if( playerInfoDoc == null )
            {
                init_acc_data(accountTableInfo);
                create_player(false,accountTableInfo);
            }
            m_login = true;
            //GameEngineMgr.Instance.player_login
        }
        else
        {
            init_acc_data(accountTableInfo);
        }

        m_login = true;

        if( is_gaming() )
        {
            on_joingame(true);
        }
        else
        {
            //创建进出日志
        }
        //创建代理房间
        GameRoomMgr.Instance.init_room(playerInfoDoc.getAgentId());
        GamePlayerMgr.Instance.addPlayerById(this);
        m_state = e_player_state.e_ps_playing;

        Client2WorldProtocol.packetw2c_player_connect_result.Builder builder = Client2WorldProtocol.packetw2c_player_connect_result.newBuilder();
        builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_success_VALUE);
        builder.setGaming(get_gameid());
        builder.setServertime(TimeHelper.Instance.get_cur_time());
        send_msg_to_client(builder.build(),builder.getPacketId().getNumber());
    }


    //同步账号信息
    private void init_acc_data(AccountTableDoc accountTableInfo)
    {
        if( playerInfoDoc  != null )
        {
            playerInfoDoc.setChannelId(accountTableInfo.getChannelId());
            playerInfoDoc.setAgentId(accountTableInfo.getAgentId());
        }
    }

    public void set_sessionid(int sessionId)
    {
        this.m_sessionid = sessionId;
    }

    public int get_sessionid()
    {
        return m_sessionid;
    }

    public e_player_state get_state()
    {
        return m_state;
    }

    public boolean load_player()
    {
        Query query = new Query(Criteria.where("Account").is(accountTableInfoDoc.getAccount()));
        PlayerInfoDoc playerInfoDoc =  DbPlayer.Instance.getMongoTemplate().findOne(query,PlayerInfoDoc.class);
        if( playerInfoDoc == null )
        {
            return false;
        }

        this.playerInfoDoc = playerInfoDoc;
        return true;
    }

    public boolean loadPlayer(PlayerInfoDoc playerInfoDoc)
    {
        this.playerInfoDoc = playerInfoDoc;
        return  true;
    }

    private void create_player(boolean isRobot, AccountTableDoc accountTableInfo)
    {
        playerInfoDoc = new PlayerInfoDoc();
        init_acc_data(accountTableInfo);
        playerInfoDoc.setPlayerId( GamePlayerMgr.Instance.generic_playerid() );
        Date date = new Date();
        date.setTime(TimeHelper.Instance.get_cur_ms());
        playerInfoDoc.setCreateTime( date );
        playerInfoDoc.setLastDayTime(TimeInt.create(TimeHelper.Instance.get_cur_time()));

        set_default_head();

        playerInfoDoc.setNickName("nickName");
        playerInfoDoc.setChannelId(accountTableInfo.getChannelId());
        playerInfoDoc.setGold(100000);
        playerInfoDoc.setLevel(0);
        playerInfoDoc.setRobot(false);

        Document doc = playerInfoDoc.to_bson(true);
        Query query = Query.query(Criteria.where("Account").is(accountTableInfo.getAccount()));

        DbPlayer.Instance.getMongoTemplate().upsert(query, Update.fromDocument(doc),PlayerInfoDoc.class);
    }

    private void set_default_head()
    {

    }

    public boolean is_gaming()
    {
        return m_gameid > 0;
    }

    public boolean join_game(int gameId, int serverId, int roomId)
    {
        return join_game(gameId,serverId,roomId,-1,null);
    }

    public boolean join_game(int gameId, int serverId, int roomId, int roomNum, ByteString gameCfgBytes)
    {
        boolean res = setGameIdServerId(gameId,serverId,roomId);
        if(!res)
        {
            return false;
        }

        if( m_logicPeer != null )
        {
            Logic2WorldProtocol.packetw2l_player_login.Builder builder = Logic2WorldProtocol.packetw2l_player_login.newBuilder();
            builder.setRoomid(roomId);
            builder.setSessionid(m_sessionid);
            msg_info_def.MsgInfoDef.msg_account_info.Builder msgAccountInfoBuilder = builder.getAccountInfoBuilder();
            msgAccountInfoBuilder.setChannelId(playerInfoDoc.getChannelId());
            msgAccountInfoBuilder.setAid(playerInfoDoc.getPlayerId());
            msgAccountInfoBuilder.setGold(playerInfoDoc.getGold());
            msgAccountInfoBuilder.setNickname(playerInfoDoc.getNickName());
            msgAccountInfoBuilder.setViplvl(playerInfoDoc.getVipLevel());
            msgAccountInfoBuilder.setRecharged(playerInfoDoc.getRecharged());
            msgAccountInfoBuilder.setSex(playerInfoDoc.getSex());
            msgAccountInfoBuilder.setCurPhotoFrameId(playerInfoDoc.getPhotoFrameId());
            msgAccountInfoBuilder.setIconCustom(playerInfoDoc.getIconCustom());
            msgAccountInfoBuilder.setTicket(0);
            msgAccountInfoBuilder.setCreateTime(0);
            msgAccountInfoBuilder.setPrivilege(0);
            msgAccountInfoBuilder.setRoomCard(playerInfoDoc.getRoomCard());
            msgAccountInfoBuilder.setGuildId(0);

            msg_info_def.MsgInfoDef.msg_account_info_ex.Builder msgAccountInfoExBuilder =  builder.getAccountInfoExBuilder();
            msgAccountInfoExBuilder.setIsRobot(playerInfoDoc.isRobot());
            msgAccountInfoExBuilder.setFreeGold(0);
            msgAccountInfoExBuilder.setProfit(0);

            if( roomNum > 0 )
            {
                builder.setRoomcardNumber(roomNum);
            }

            if( gameCfgBytes != null )
            {
                builder.setRoomCfg(gameCfgBytes);
            }

            m_logicPeer.send_msg(builder.build());
        }

        return true;
    }


    public MsgTypeDef.e_msg_result_def change_gold( int dif_gold,boolean needsend )
    {
        if( dif_gold == 0 )
        {
            return MsgTypeDef.e_msg_result_def.e_rmt_success;
        }

        int t = playerInfoDoc.getGold();
        int old_value = t;
        if( dif_gold > 0 )
        {

        }
        else
        {
            t += dif_gold;
        }

        if( t < 0 )
        {
            t = 0;
        }

        playerInfoDoc.setGold(t);

        if( needsend )
        {
            if( m_logicPeer == null )
            {
                return MsgTypeDef.e_msg_result_def.e_rmt_success;
            }

            Logic2WorldProtocol.packetw2l_change_player_property.Builder builder = Logic2WorldProtocol.packetw2l_change_player_property.newBuilder();
            builder.setPlayerid(playerInfoDoc.getPlayerId());
            builder.getChangeInfoBuilder().setGold(dif_gold);
            m_logicPeer.send_msg(builder);

        }

        return MsgTypeDef.e_msg_result_def.e_rmt_success;
    }


    private boolean setGameIdServerId(int gameId,int serverId,int roomId)
    {
        if( m_logicid != 0)
        {
            return false;
        }

        m_logicid = serverId;
        m_gameid = gameId;
        m_roomId = roomId;
        if( !playerInfoDoc.isRobot() )
        {
            GameEngineMgr.Instance.update_game_info(gameId,serverId,true);
        }

        GamePlayerMgr.Instance.onEnterGame(m_gameid);
        reset_logicpeer();
        return true;
    }

    public boolean resetGameIdServerId()
    {
        if( m_logicid == 0 )
        {
            return false;
        }

        GamePlayerMgr.Instance.onExitGame(m_gameid);

        if( !playerInfoDoc.isRobot() )
        {
            GameEngineMgr.Instance.update_game_info(m_gameid,m_logicid,false);
        }

        m_logicid = 0;
        m_gameid = 0;
        m_roomId = 0;
        reset_logicpeer();

        return true;
    }

    public boolean on_joingame( boolean blogin )
    {
        if(blogin)
        {
            if( m_logicPeer != null )
            {
                Logic2WorldProtocol.packetw2l_player_login.Builder builder = Logic2WorldProtocol.packetw2l_player_login.newBuilder();
                builder.getAccountInfoBuilder().setAid(playerInfoDoc.getPlayerId());
                builder.setSessionid(m_sessionid);
                m_logicPeer.send_msg(builder);
            }
        }
        else
        {
            if( !playerInfoDoc.isRobot())
            {
                playerInfoDoc.setLastGameId(m_gameid);
            }
        }

        if( m_gatePeer != null && m_logicid > 0)
        {
            ServerProtocol.packet_player_connect.Builder builder = ServerProtocol.packet_player_connect.newBuilder();
            builder.setLogicid(m_logicid);
            builder.setSessionid(m_sessionid);
            m_gatePeer.send_msg(builder);
        }

        return true;
    }


    public void leave_game()
    {
        if( !resetGameIdServerId() )
        {
            return;
        }

        //告诉网关服务器，某个玩家不在某个子游戏上面了
        if( m_gatePeer != null )
        {
            ServerProtocol.packet_player_connect.Builder builder =  ServerProtocol.packet_player_connect.newBuilder();
            builder.setLogicid(m_logicid);
            builder.setSessionid(m_sessionid);
            m_gatePeer.send_msg(builder);
        }
    }

    public void clear_logic(int lid)
    {
        if( lid == 0 || lid == m_logicid )
        {
            m_logicid = 0;
            m_logicPeer = null;
        }
    }

    public void reset_logicpeer()
    {
        m_logicPeer = null;
        if( m_logicid <= 0 )
        {
            return;
        }

        m_logicPeer = ServersManager.Instance.find_server(m_logicid);
    }

    public int send_msg_to_client(Message.Builder builder)
    {
        return send_msg_to_client(builder.build());
    }

    public int send_msg_to_client(Message msg)
    {
        int msgId = ProtocolClassUtils.getProtocolByClass(msg.getClass());
        return send_msg_to_client(msg,msgId);
    }

    public int send_msg_to_client(Message msg,int packetId)
    {
        if( m_gatePeer == null )
        {
            return -1;
        }

        ServerProtocol.packet_transmit_msg.Builder builder = ServerProtocol.packet_transmit_msg.newBuilder();
        builder.setSessionid(m_sessionid);
        server_protocols.ServerBase.msg_packet.Builder msgPackBuilder =  builder.getMsgpakBuilder();

        msgPackBuilder.setMsgid(packetId);
        msgPackBuilder.setMsginfo(msg.toByteString());
        builder.setMsgpak(msgPackBuilder);

        return m_gatePeer.send_msg(builder.build());
    }


    private void reset_gatepeer()
    {
        m_gatePeer = null;
        if( m_sessionid <= 0 )
        {
            return;
        }

        m_gatePeer =  ServersManager.Instance.find_server(SessionHelper.get_gateid(m_sessionid));
    }

    public WorldPeer get_logic()
    {
        return  m_logicPeer;
    }

    public WorldPeer get_gate()
    {
        return m_gatePeer;
    }

    public int get_logicid()
    {
        return m_logicid;
    }

    public int get_gameid()
    {
        return m_gameid;
    }

    public void clear_gate_logicid()
    {
        if(playerInfoDoc.isRobot())
        {
            return;
        }

        if( m_gatePeer == null )
        {
            return;
        }

        ServerProtocol.packet_player_connect.Builder builder = ServerProtocol.packet_player_connect.newBuilder();
        builder.setLogicid(0);
        builder.setSessionid(m_sessionid);
        m_gatePeer.send_msg(builder.build());
    }



    public PlayerInfoDoc getPlayerInfoDoc() {
        return playerInfoDoc;
    }

    public void setPlayerInfoDoc(PlayerInfoDoc playerInfoDoc) {
        this.playerInfoDoc = playerInfoDoc;
    }

    public AccountTableDoc getAccountTableInfoDoc() {
        return accountTableInfoDoc;
    }

    public void setAccountTableInfoDoc(AccountTableDoc accountTableInfoDoc) {
        this.accountTableInfoDoc = accountTableInfoDoc;
    }

    private void store_game_object()
    {

    }

    public enum e_player_state
    {
        e_ps_none,
        e_ps_playing,
        e_ps_disconnect,
    }
}
