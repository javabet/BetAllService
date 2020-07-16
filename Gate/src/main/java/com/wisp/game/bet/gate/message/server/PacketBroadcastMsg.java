package com.wisp.game.bet.gate.message.server;


import com.wisp.game.bet.gate.services.ClientManager;
import com.wisp.game.bet.gate.services.GatePeer;
import com.wisp.game.bet.gate.services.ServerPeer;
import com.wisp.game.share.netty.IRequest;
import com.wisp.game.share.netty.PacketManager.DefaultRequestMessage;
import com.wisp.game.share.utils.SessionHelper;
import server_protocols.ServerProtocol;

@IRequest
public class PacketBroadcastMsg extends DefaultRequestMessage<ServerProtocol.packet_broadcast_msg, ServerPeer> {


    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_broadcast_msg msg) {

        for( int i = 0; i < msg.getSessionidsCount();i++)
        {
            GatePeer gatePeer = ClientManager.Instance.find_objr(SessionHelper.get_peerid(msg.getSessionids(i)));
            if( gatePeer != null )
            {
                 gatePeer.send_msg(msg.getMsgpak().getMsgid(),msg.getMsgpak().toByteArray());
            }
        }

        return true;
    }
}
