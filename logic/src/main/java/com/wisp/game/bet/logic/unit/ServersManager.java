package com.wisp.game.bet.logic.unit;


import com.google.protobuf.Message;
import com.wisp.game.bet.logic.gameMgr.GameManager;
import com.wisp.game.bet.share.common.EnableObjectManager;
import com.wisp.game.bet.share.utils.ProtocolClassUtils;
import com.wisp.game.bet.share.utils.SessionHelper;
import org.springframework.stereotype.Component;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ServersManager extends EnableObjectManager<Integer,LogicPeer> {

    public static ServersManager Instance;

    private int m_last_bot = 0;
    private ConcurrentHashMap<Integer, LogicPeer> servers_map = new ConcurrentHashMap<>();
    private List<LogicPeer> botVector = new ArrayList<>();

    public ServersManager() {
        Instance = this;
    }

    public boolean regedit_server( LogicPeer logicPeer )
    {
        if( logicPeer.get_remote_type() == ServerBase.e_server_type.e_st_robot_VALUE)
        {
            botVector.add(logicPeer);
            GameManager.Instance.notify_bot_state(true);
            return true;
        }
        else
        {
            boolean hasRet = hasKey(logicPeer.get_id());
            if( !hasRet )
            {
                return false;
            }

            hasRet =  servers_map.containsKey(logicPeer.get_remote_id());
            if( !hasRet )
            {
                servers_map.put(logicPeer.get_remote_id(),logicPeer);
                return true;
            }
        }

       return false;
    }

    public boolean remove_server( LogicPeer logicPeer )
    {
        if( logicPeer.get_remote_type() == ServerBase.e_server_type.e_st_robot_VALUE )
        {
            int idx =  botVector.indexOf(logicPeer);
            if( idx >= 0 )
            {
                botVector.remove(idx);
                GameManager.Instance.notify_bot_state(false);
            }
        }
        else
        {
            servers_map.remove(logicPeer.get_remote_id());
        }

        return remove_obj(logicPeer.get_id());
    }

    public LogicPeer find_server( int serverid )
    {
        return servers_map.get(serverid);
    }


    public LogicPeer get_bot()
    {
        if( m_last_bot >= botVector.size() )
        {
            m_last_bot = 0;
        }

        if( m_last_bot < botVector.size() )
        {
            return botVector.get(m_last_bot);
        }


        return null;
    }

    public void next_bot()
    {
        m_last_bot ++;
    }

    public void heartbeat( double elapsed )
    {
        for( LogicPeer logicPeer : obj_map.values() )
        {
            logicPeer.heartbeat(elapsed);
        }
    }

    public void broadcast_msg( Message msg)
    {
        int packetId = ProtocolClassUtils.getProtocolByClass(msg.getClass());
        this.broadcast_msg(packetId,msg,-1);
    }

    public void broadcast_msg(int packetId, Message msg)
    {
        this.broadcast_msg(packetId,msg,-1);
    }

    public void broadcast_msg(int packetId, Message msg,int except_id)
    {
        for(LogicPeer logicPeer : obj_map.values())
        {
            if( logicPeer.get_id() != except_id)
            {
                continue;
            }

            logicPeer.send_msg(packetId,msg);
        }
    }

    public  void shutdown()
    {
        ServerProtocol.packet_serverdown.Builder builder = ServerProtocol.packet_serverdown.newBuilder();

        for( LogicPeer logicPeer : botVector)
        {
            logicPeer.send_msg(builder.build());
        }
    }


    public int send_msg_to_client(int sessionid,int packetId,Message msg)
    {
        int gateId = SessionHelper.get_gateid(sessionid);
        LogicPeer logicPeer =  find_server(gateId);
        if( logicPeer != null )
        {
            ServerProtocol.packet_transmit_msg.Builder builder = ServerProtocol.packet_transmit_msg.newBuilder();
            builder.setSessionid(sessionid);
            server_protocols.ServerBase.msg_packet.Builder msgPacketBuilder = builder.getMsgpakBuilder();
            msgPacketBuilder.setMsgid(packetId);
            msgPacketBuilder.setMsginfo(msg.toByteString());

            return logicPeer.send_msg(builder.build());
        }

        return -1;
    }

    public int send_msg_to_client(List<Integer> sessionIds,int packetId,Message msg )
    {
        Map<Integer,List<Integer>> gateList = new HashMap<>();

        for(Integer sessionId :sessionIds)
        {
            int gateId = SessionHelper.get_gateid(sessionId);
            if( gateList.containsKey(gateId) )
            {
                gateList.get(gateId).add(sessionId);
            }
            else
            {
                List<Integer> list = new ArrayList<>();
                list.add(sessionId);
                gateList.put(gateId,list);
            }
        }

        for( Integer gateId : gateList.keySet() )
        {
            LogicPeer logicPeer = find_server(gateId);
            if( logicPeer == null )
            {
                continue;
            }

            ServerProtocol.packet_broadcast_msg.Builder builder =  ServerProtocol.packet_broadcast_msg.newBuilder();
            builder.addAllSessionids(gateList.get(gateId));

            builder.getMsgpakBuilder().setMsgid(packetId);
            builder.getMsgpakBuilder().setMsginfo(msg.toByteString());
            logicPeer.send_msg( builder.build() );
        }

        return 0;
    }

    public void send_msg_to_bot(List<Integer> pids,int packetId,Message msg)
    {
        Map<Integer,List<Integer>> botsMap = new HashMap<>() ;

        //TODO wisp
    }


}
