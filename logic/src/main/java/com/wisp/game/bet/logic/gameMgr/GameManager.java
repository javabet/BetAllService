package com.wisp.game.bet.logic.gameMgr;

import client2gate_protocols.Client2GateProtocol;
import com.google.protobuf.Message;
import com.wisp.game.bet.GameConfig.MainBaseConfig;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.sshare.IGameEngine;
import com.wisp.game.bet.logic.sshare.MsgPacketOne;
import com.wisp.game.bet.logic.unit.*;
import com.wisp.game.bet.share.utils.ProtocolClassUtils;
import logic2world_protocols.Logic2WorldProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameManager {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public static GameManager Instance;

    private boolean m_is_shutdowning = false;
    private double m_force_time = 0;
    private double mCheckTime = 0;
    private  int m_gameid = 0;
    private  int m_gamever = 0;
    @Autowired
    private RobotManager robotmgr;

    @Resource
    private IGameEngine gameEngine;


    public GameManager() {
        Instance = this;
    }


    public boolean open()
    {
        game_regedit();
        return true;
    }


    public IGameEngine get_game_engine()
    {
        return gameEngine;
    }


    public void on_init_engine(int gameId,String game_ver)
    {
        m_gameid = gameId;
        m_gamever = 100;

        robotmgr.set_gameid(gameId);
        //有重复，不需要重复发送信息
        game_regedit();
    }

    public int get_gameid()
    {
        return m_gameid;
    }

    public int get_gamever()
    {
        return  m_gamever;
    }

    public void reflush_game_room()
    {

    }



    public void game_regedit()
    {
        if( m_gameid <= 0 )
        {
            return;
        }

        ConcurrentHashMap<Integer, ServerPeer> world_map = BackstageManager.Instance.get_world_map();

        for( ServerPeer serverPeer : world_map.values() )
        {
            Logic2WorldProtocol.packetl2w_game_ready.Builder builder = Logic2WorldProtocol.packetl2w_game_ready.newBuilder();
            builder.setGameId(m_gameid);
            builder.setGameVer(m_gamever);
            serverPeer.send_msg(builder.build());
        }
    }



    //请求机器人 请求的机器人不一定及时返回
    //要求的vip 要求的gold  自定义标志tag
    public void request_robot(int tag, int needgold, boolean exroom)
    {
        if (is_shutdowning())
            return;

        int playerid = robotmgr.request_robot(tag, needgold, exroom);
        if(playerid >0)//找到机器人及时返回
        {
            robotmgr.inc_robot();
            gameEngine.response_robot(playerid, tag);
        }
    }

    public void request_robot(int tag, boolean banker, int bet_rate, boolean exroom)
    {
        if (is_shutdowning())
            return;

        int playerid = robotmgr.request_robot(tag, banker, bet_rate, exroom);
        if(playerid >0)//找到机器人及时返回
        {
            robotmgr.inc_robot();
            gameEngine.response_robot(playerid, tag);
        }
    }

    public void request_bot(int tag, int needgold, int needvip)
    {
        if (is_shutdowning())
            return;

        int playerid = robotmgr.request_bot(tag, needgold, needvip);
        if(playerid >0)//找到机器人及时返回
        {
            robotmgr.inc_robot();
            gameEngine.response_robot(playerid, tag);
        }
    }

    public void next_bot()
    {
        ServersManager.Instance.next_bot();
    }

    public int get_bet_robot_count(int tag, boolean exroom)
    {
        return robotmgr.get_bet_robot_count(tag, exroom);
    }

    public int get_banker_robot_count(int tag, boolean exroom)
    {
        return robotmgr.get_banker_robot_count(tag, exroom);
    }

    public void kick_out( int playerId )
    {
        GamePlayer player =  GamePlayerMgr.Instance.find_playerbyid(playerId);
        if( player == null )
        {
            return;
        }

        if( player.leave_game(false) )
        {
            GamePlayerMgr.Instance.remove_player(player);
        }
    }

    public void shutdown_kick_player(int playerId)
    {
        GamePlayer gamePlayer  = GamePlayerMgr.Instance.find_playerbyid(playerId);
        if( gamePlayer == null )
        {
            return;
        }

        gamePlayer.leave_game(true);
        GamePlayerMgr.Instance.remove_player(gamePlayer);
    }

    public boolean is_shutdowning()
    {
        return m_is_shutdowning;
    }

    //当不需要使用机器人时 只要退出到房间选择然后调用此函数
    public void release_robot(int playerid)
    {
        robotmgr.release_robot(playerid);
    }

    public void heartbeat( double elapsed )
    {
        robotmgr.heartbeat(elapsed);

        if( m_force_time > 0 )
        {
            m_force_time -= elapsed;
            if( m_force_time < 0 )
            {
                serverdown();
            }
        }
    }

    public void prepare_shutdown( int gameId )
    {
        logger.warn("game_manager prepare shutdown game id:" + m_gameid);

        if( gameId == 0 || m_gameid == gameId )
        {
            m_is_shutdowning = true;
            m_force_time = 60;
        }
    }

    public void serverdown()
    {
        logger.warn("game_manager start serverdown");
        m_force_time = -1;

        GamePlayerMgr.Instance.shutdown();
        ServersManager.Instance.shutdown();

        logger.warn("game_manager finish serverdown");
    }

    public void notify_bot_state( boolean connected )
    {
        if( gameEngine != null )
        {
            gameEngine.notify_bot_state(connected);
        }
    }

    public int broadcast_msg_to_client(List<Integer> pids,Message msg)
    {
        int packetId = ProtocolClassUtils.getProtocolByClass(msg.getClass());

        return broadcast_msg_to_client(pids,packetId,msg);
    }

    public int broadcast_msg_to_client(List<Integer> pids, int packetId, Message msg)
    {
        List<Integer> sids = new ArrayList<>();
        for( int i = 0; i < pids.size();i++ )
        {
            GamePlayer gamePlayer = GamePlayerMgr.Instance.find_playerbyid(pids.get(i));
            if( gamePlayer != null && gamePlayer.get_sessionid() > 0 )
            {
                sids.add(gamePlayer.get_sessionid());
            }
        }

        return ServersManager.Instance.send_msg_to_client(sids,packetId,msg);
    }

    public void broadcast_msg_to_bot( List<Integer> pids,Message msg )
    {
        int packetId = ProtocolClassUtils.getProtocolByClass(msg.getClass());
        broadcast_msg_to_bot(pids,packetId,msg);
    }

    public void broadcast_msg_to_bot( List<Integer> pids,int packet_id,Message msg )
    {
        ServersManager.Instance.send_msg_to_bot(pids,packet_id,msg);
    }

    public void send_msg_to_world(int worldId,int packet_id, Message msg )
    {
        ServerPeer serverPeer = BackstageManager.Instance.get_world_byid( worldId );
        if( serverPeer != null )
        {
            serverPeer.send_msg(packet_id,msg);
        }
    }


    public int broadcast_msglist_to_client( List<Integer> pids,List<MsgPacketOne> msgList )
    {
        List<Integer> sids = new ArrayList<>();
        for(int i = 0; i < pids.size();i++)
        {
            GamePlayer gamePlayer =  GamePlayerMgr.Instance.find_playerbyid(pids.get(i));
            if( gamePlayer != null && gamePlayer.get_sessionid() > 0 )
            {
                sids.add( gamePlayer.get_sessionid() );
            }
        }

        Client2GateProtocol.packet_g2c_send_msglist.Builder builder =  Client2GateProtocol.packet_g2c_send_msglist.newBuilder();
        for( int i = 0; i < msgList.size();i++ )
        {
            client2gate_protocols.Client2GateProtocol.msg_packet.Builder msg_packet_builder = client2gate_protocols.Client2GateProtocol.msg_packet.newBuilder();
            msg_packet_builder.setMsgid(msgList.get(i).getPacket_id());
            msg_packet_builder.setMsginfo(msgList.get(i).getMsg_packet().toByteString());
            builder.addMsgpaks(  msg_packet_builder.build() );
        }

        return ServersManager.Instance.send_msg_to_client(sids,builder);
    }

    public void notify_close_room(int agent_id, int room_id)
    {
        /**
        auto sendmsg = PACKET_CREATE(packetl2gs_room_close, e_mst_l2gs_room_close);
        sendmsg->set_game_id(get_gameid());
        sendmsg->set_agent_id(agent_id);
        sendmsg->set_room_id(room_id);

        auto& map = backstage_manager::instance().get_gstate_map();
        for (auto& it : map)
        {
            if (it.second)
            {
                it.second->send_msg(sendmsg);
            }
        }
        **/

    }

    public int get_serverid()
    {
        return LogicServer.Instance.get_serverid();
    }

    public boolean open_room()
    {
        boolean open = true;

        return open;
    }

    public boolean is_room_config_check_act()
    {
        MainBaseConfig.MainBaseConfigData data =  MainBaseConfig.GetInstnace().GetData("RoomConfigCheck");


        return data != null && data.getmValue() > 0;
    }

    public void on_exit_engine()
    {

    }

}
