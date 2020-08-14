package com.wisp.game.bet.logic.proc.logic;

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
public class PacketBotTransmitMsg extends DefaultRequestMessage<ServerProtocol.packet_bot_transmit_msg, LogicPeer> {
    @Override
    public boolean packet_process(LogicPeer peer, ServerProtocol.packet_bot_transmit_msg msg) {
        RequestMessageRegister.ProtocolStruct protocolStruct =   RequestMessageRegister.Instance.getProtocolStruct(msg.getMsgpak().getMsgid());
        boolean bret = false;
        if( protocolStruct != null )
        {
            Message innerMsg = RequestMessageRegister.Instance.getMessageByProtocolId(msg.getPacketId().getNumber(),msg.toByteArray());

            IRequestMessage requestMessage =  protocolStruct.getHandlerInstance();

            GamePlayer player = GamePlayerMgr.Instance.find_player( msg.getPlayerid() );
            if( player != null && player.get_state() == e_player_state.e_ps_playing )
            {
                bret = requestMessage.packet_process(peer,player,innerMsg);
            }
        }

        if( !bret )
        {
           //do nothing
        }

        return true;
    }
}
