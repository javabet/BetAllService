package com.wisp.game.bet.gate.message.server;

import com.wisp.game.bet.gate.services.ClientManager;
import com.wisp.game.bet.gate.services.ServerPeer;
import com.wisp.game.share.netty.IRequest;
import com.wisp.game.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerProtocol;

@IRequest
public class PacketRegeditRouteMsg extends DefaultRequestMessage<ServerProtocol.packet_regedit_route_msg, ServerPeer> {


    @Override
    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_regedit_route_msg msg) {

        for( int i = 0; i <  msg.getMsgidsCount(); i ++ )
        {
            ClientManager.Instance.regedit_msg(msg.getMsgids(i),peer.get_remote_type());
        }

        return true;
    }
}
