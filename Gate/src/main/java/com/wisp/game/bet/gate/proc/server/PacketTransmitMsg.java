package com.wisp.game.bet.gate.proc.server;

import com.wisp.game.bet.gate.unit.ClientManager;
import com.wisp.game.bet.gate.unit.GatePeer;
import com.wisp.game.bet.gate.unit.ServerPeer;
import com.wisp.game.share.netty.IRequest;
import com.wisp.game.share.netty.PacketManager.DefaultRequestMessage;
import com.wisp.game.share.utils.SessionHelper;
import server_protocols.ServerProtocol;

@IRequest
public class PacketTransmitMsg extends DefaultRequestMessage<ServerProtocol.packet_transmit_msg, ServerPeer> {

    @Override
    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_transmit_msg msg) {

        int sessionId = SessionHelper.get_peerid(msg.getSessionid());
        GatePeer gatePeer = ClientManager.Instance.find_objr(sessionId);
        if( gatePeer != null )
        {
            gatePeer.send_msg(msg.getMsgpak().getMsgid(),msg.getMsgpak().toByteArray());
        }

        return true;
    }
}