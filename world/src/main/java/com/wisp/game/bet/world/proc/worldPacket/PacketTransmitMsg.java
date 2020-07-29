package com.wisp.game.bet.world.proc.worldPacket;

import com.google.protobuf.Message;
import com.wisp.game.bet.world.PlayerSys.GamePlayer;
import com.wisp.game.bet.world.gameMgr.GamePlayerMgr;
import com.wisp.game.bet.world.unit.WorldPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import com.wisp.game.bet.share.netty.PacketManager.IRequestMessage;
import com.wisp.game.bet.share.netty.RequestMessageRegister;
import server_protocols.ServerProtocol;

@IRequest
public class PacketTransmitMsg extends DefaultRequestMessage<ServerProtocol.packet_transmit_msg, WorldPeer> {
    @Override
    public boolean packet_process(WorldPeer peer, ServerProtocol.packet_transmit_msg msg) {

        RequestMessageRegister.ProtocolStruct protocolStruct =   RequestMessageRegister.Instance.getProtocolStruct(msg.getMsgpak().getMsgid());
        boolean bret = false;
        if( protocolStruct != null )
        {
            Message innerMsg = RequestMessageRegister.Instance.getMessageByProtocolId(msg.getPacketId().getNumber(),msg.toByteArray());

            IRequestMessage requestMessage =  protocolStruct.getHandlerInstance();
            if( requestMessage.use_sessionid() )
            {
                bret = requestMessage.packet_process(peer,msg.getSessionid(),innerMsg);
            }
            else
            {
                GamePlayer player = GamePlayerMgr.Instance.find_player( msg.getSessionid() );
                if( player != null )
                {
                    bret = requestMessage.packet_process(peer,player,innerMsg);
                }
            }
        }

        if( !bret )
        {
            logger.error("packet_transmit_msg error id:" + msg.getMsgpak().getMsgid() + " serverId:" + peer.get_remote_id() + " sessionId:" + msg.getSessionid());
            ServerProtocol.packet_player_disconnect.Builder builder =  ServerProtocol.packet_player_disconnect.newBuilder();
            builder.setSessionid(msg.getSessionid());
            peer.send_msg(builder.build());
        }

        return true;
    }
}
