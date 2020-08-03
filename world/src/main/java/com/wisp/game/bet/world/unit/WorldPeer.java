package com.wisp.game.bet.world.unit;


import com.google.protobuf.Message;
import com.wisp.game.bet.share.netty.PeerTcp;
import com.wisp.game.bet.share.utils.ProtocolClassUtils;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

import java.util.List;

//World作为Netty服务器时，外部连接此时
public class WorldPeer extends PeerTcp {

    private int m_gameid = -1;

    public void heartbeat( double elapsed )
    {
        int packet_id = packet_service(-1);
        if(packet_id != 0 )
        {
            logger.error("monitor_peer packet_service error id:" + get_id() + " packetid:" + packet_id);
            return;
        }
    }


    public int send_msg_to_client(int sessionid,  Message.Builder builder)
    {
        return  send_msg_to_client(sessionid,builder.build());
    }

    public int send_msg_to_client(int sessionid,  Message msg)
    {
        int packetId = ProtocolClassUtils.getProtocolByClass(msg.getClass());
        return  send_msg_to_client(sessionid,packetId,msg);
    }


    public int send_msg_to_client(int sessionid, int packet_id, Message msg)
    {
        ServerProtocol.packet_transmit_msg.Builder builder = ServerProtocol.packet_transmit_msg.newBuilder();
        builder.setSessionid(sessionid);
        server_protocols.ServerBase.msg_packet.Builder packetBuilder =  builder.getMsgpakBuilder();
        packetBuilder.setMsginfo(msg.toByteString());
        packetBuilder.setMsgid(packet_id);

        return  send_msg(builder.build());
    }

    public int send_msg_to_client(List<Integer> sessinIds, Message.Builder builder )
    {
        Message msg = builder.build();
        int packetId = ProtocolClassUtils.getProtocolByClass(msg.getClass());
        return send_msg_to_client(sessinIds,packetId,msg);
    }

    public int send_msg_to_client(List<Integer> sessinIds, Message msg )
    {
        int packetId = ProtocolClassUtils.getProtocolByClass(msg.getClass());
        return send_msg_to_client(sessinIds,packetId,msg);
    }

    public int send_msg_to_client(List<Integer> sessinIds,int packet_id, Message message )
    {
        ServerProtocol.packet_broadcast_msg.Builder builder = ServerProtocol.packet_broadcast_msg.newBuilder();
        builder.addAllSessionids(sessinIds);
        server_protocols.ServerBase.msg_packet.Builder packetBuilder = builder.getMsgpakBuilder();
        packetBuilder.setMsgid(packet_id);
        packetBuilder.setMsginfo(message.toByteString());

        return send_msg(builder.build());
    }



    @Override
    public int get_type() {
        return ServerBase.e_server_type.e_st_world_VALUE;
    }

    public int get_gameid()
    {
        return m_gameid;
    }
}
