package com.wisp.game.bet.logic.unit;

import com.google.protobuf.Message;
import com.wisp.game.bet.share.netty.PeerTcp;
import com.wisp.game.bet.share.utils.ProtocolClassUtils;
import io.netty.channel.ChannelHandlerContext;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

import java.util.List;


public class LogicPeer extends PeerTcp {

    public LogicPeer(ChannelHandlerContext channelHandlerContext,int peerId) {
        initChannelHandlerContext(channelHandlerContext);
        m_id = peerId;
    }

    @Override
    public int get_type() {
        return ServerBase.e_server_type.e_st_logic_VALUE;
    }

    public void heartbeat(double elapsed)
    {
        int packetId = packet_service(-1);
        if( packetId != 0 )
        {
            logger.error("monitor_peer packet_service error id:" + get_id() + " packetId:" + packetId );
            return;
        }
    }

    @Override
    public int packet_service(int process_count) {

        if( this.receive_queue.size() >  0)
        {
            System.out.printf("the receive_queue go this..");
        }

        return super.packet_service(process_count);
    }

    public int send_msg_to_client(int sessionId, Message msg)
    {
        int packetId = ProtocolClassUtils.getProtocolByClass(msg.getClass());
        return send_msg_to_client(sessionId,packetId,msg);
    }

    public int send_msg_to_client(int sessionId, int packetId, Message msg)
    {
        ServerProtocol.packet_transmit_msg.Builder builder =  ServerProtocol.packet_transmit_msg.newBuilder();
        builder.setSessionid(sessionId);
        server_protocols.ServerBase.msg_packet.Builder msgpacktBuilder =  builder.getMsgpakBuilder();
        msgpacktBuilder.setMsgid(packetId);
        msgpacktBuilder.setMsginfo(msg.toByteString());

        return send_msg(builder.build());
    }

    public int send_msg_to_client(List<Integer> sessionIds,Message msg)
    {
        int packetId = ProtocolClassUtils.getProtocolByClass(msg.getClass());
        return send_msg_to_client(sessionIds,packetId,msg);
    }

    public int send_msg_to_client(List<Integer> sessionIds,int packetId,Message msg)
    {
        ServerProtocol.packet_broadcast_msg.Builder builder = ServerProtocol.packet_broadcast_msg.newBuilder();
        builder.addAllSessionids(sessionIds);

        server_protocols.ServerBase.msg_packet.Builder msgPacketBuilder = builder.getMsgpakBuilder();
        msgPacketBuilder.setMsgid(packetId);
        msgPacketBuilder.setMsginfo(msg.toByteString());

        return send_msg(builder.build());
    }

}
