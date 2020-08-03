package com.wisp.game.bet.logic.proc.init;


import com.google.protobuf.Message;
import com.wisp.game.bet.logic.gameMgr.GamePlayerMgr;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.sshare.e_player_state;
import com.wisp.game.bet.logic.unit.LogicPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import com.wisp.game.bet.share.netty.PacketManager.IRequestMessage;
import com.wisp.game.bet.share.netty.RequestMessageRegister;
import server_protocols.ServerProtocol;

@IRequest
public class PacketTransmitMsg extends DefaultRequestMessage<ServerProtocol.packet_transmit_msg, LogicPeer> {
    @Override
    public boolean packet_process(LogicPeer peer, ServerProtocol.packet_transmit_msg msg) {

        RequestMessageRegister.ProtocolStruct protocolStruct =   RequestMessageRegister.Instance.getProtocolStruct(msg.getMsgpak().getMsgid());
        boolean bret = false;
        if( protocolStruct != null )
        {
            Message innerMsg = RequestMessageRegister.Instance.getMessageByProtocolId(msg.getMsgpak().getMsgid(),msg.getMsgpak().getMsginfo());

            IRequestMessage requestMessage =  protocolStruct.getHandlerInstance();
            if( requestMessage.use_sessionid() )
            {
                bret = requestMessage.packet_process(peer,msg.getSessionid(),innerMsg);
            }
            else
            {
                GamePlayer player = GamePlayerMgr.Instance.find_player( msg.getSessionid() );
                if( player != null && player.get_state() == e_player_state.e_ps_playing )
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
