package com.wisp.game.bet.world.PlayerSys;

import client2world_protocols.Client2WorldProtocol;
import com.google.protobuf.Message;
import com.wisp.game.bet.db.mongo.account.info.AccountTableDoc;
import com.wisp.game.bet.db.mongo.player.info.PlayerInfoDoc;
import com.wisp.game.bet.share.component.TimeHelper;
import com.wisp.game.bet.share.utils.ProtocolClassUtils;
import com.wisp.game.bet.share.utils.SessionHelper;
import com.wisp.game.bet.world.db.DbPlayer;
import com.wisp.game.bet.world.unit.ServersManager;
import com.wisp.game.bet.world.unit.WorldPeer;
import msg_type_def.MsgTypeDef;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import server_protocols.ServerProtocol;

public class GamePlayer {

    private boolean  m_login;
    private double m_checkip_time;
    private int m_cur_tm;
    private int m_sessionid;
    private int m_logicid;
    private int m_gameid;
    private e_player_state m_state;

    private int m_check_save;
    private boolean is_relogin;

    private WorldPeer m_worldPeer;

    private PlayerInfoDoc m_playerInfo;
    private AccountTableDoc m_accountTableInfo;

    public GamePlayer() {
        this.m_playerInfo = new PlayerInfoDoc();
    }

    public int get_sessionid()
    {
        return 0;
    }

    public void player_logout()
    {

    }

    public void set_sessionid(int sessionId)
    {
        this.m_sessionid = sessionId;
    }

    public void player_login(AccountTableDoc accountTableInfo, String platfrom, String loginPlatform, boolean isRelogin, String csToken)
    {
        Query query = new Query(Criteria.where("Account").is(accountTableInfo.getAccount()));
        PlayerInfoDoc playerInfoRet = DbPlayer.Instance.getMongoTemplate().findOne(query, PlayerInfoDoc.class);

        this.m_accountTableInfo = accountTableInfo;

        reset_gatepeer();

        if( !is_relogin )
        {
            boolean new_player = false;
            if( playerInfoRet == null )
            {
                new_player = true;

                init_acc_data(accountTableInfo);
                create_player(false,accountTableInfo);

            }
        }
        else
        {
            init_acc_data(accountTableInfo);
        }

        m_login = true;

        if( is_gaming() )
        {
            on_joingame();
        }
        else
        {
            //创建进出日志
        }

        m_state = e_player_state.e_ps_playing;

        Client2WorldProtocol.packetw2c_player_connect_result.Builder builder = Client2WorldProtocol.packetw2c_player_connect_result.newBuilder();
        builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_success_VALUE);
        builder.setGaming(get_gameid());
        builder.setServertime(TimeHelper.Instance.get_cur_time());
        send_msg_to_client(builder.build(),builder.getPacketId().getNumber());
    }

    private void reset_gatepeer()
    {
        m_worldPeer = null;
        if( m_sessionid <= 0 )
        {
            return;
        }

        WorldPeer worldPeer =  ServersManager.Instance.find_server(SessionHelper.get_gateid(m_sessionid));
        if( worldPeer != null )
        {
            this.m_worldPeer = worldPeer;

        }
        else
        {

        }
    }

    //同步账号信息
    private void init_acc_data(AccountTableDoc accountTableInfo)
    {

    }

    private void create_player(boolean isRobot, AccountTableDoc accountTableInfo)
    {
        PlayerInfoDoc playerInfoDoc = new PlayerInfoDoc();
        playerInfoDoc.setPlayerId( 0 );
        playerInfoDoc.setCreateTime( TimeHelper.Instance.get_cur_time() );
        playerInfoDoc.setNickName("nickName");

    }

    private void on_joingame()
    {

    }

    private int get_gameid()
    {
        return 0;
    }

    private boolean is_gaming()
    {
        return false;
    }

    public e_player_state get_state()
    {
        return m_state;
    }

    public int send_msg_to_client(Message msg)
    {
        int msgId = ProtocolClassUtils.getProtocolByClass(msg.getClass());
        return send_msg_to_client(msg,msgId);
    }

    public int send_msg_to_client(Message msg,int packetId)
    {
        ServerProtocol.packet_transmit_msg.Builder builder = ServerProtocol.packet_transmit_msg.newBuilder();
        builder.setSessionid(m_sessionid);
        server_protocols.ServerBase.msg_packet.Builder msgPackBuilder =  builder.getMsgpakBuilder();

        msgPackBuilder.setMsgid(packetId);
        msgPackBuilder.setMsginfo(msg.toByteString());
        builder.setMsgpak(msgPackBuilder);

        return m_worldPeer.send_msg(builder.build());
    }


    public enum e_player_state
    {
        e_ps_none,
        e_ps_playing,
        e_ps_disconnect,
    }
}
