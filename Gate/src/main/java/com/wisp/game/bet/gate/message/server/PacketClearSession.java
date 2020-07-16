package com.wisp.game.bet.gate.message.server;

import com.wisp.game.bet.gate.services.ClientManager;
import com.wisp.game.bet.gate.services.GatePeer;
import com.wisp.game.bet.gate.services.GateServer;
import com.wisp.game.bet.gate.services.ServerPeer;
import com.wisp.game.share.netty.IRequest;
import com.wisp.game.share.netty.PacketManager.DefaultRequestMessage;
import com.wisp.game.share.utils.SessionHelper;
import server_protocols.ServerProtocol;

@IRequest
public class PacketClearSession extends DefaultRequestMessage<ServerProtocol.packet_clear_session, ServerPeer> {

    @Override
    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_clear_session msg) {
        int peerid = SessionHelper.get_peerid(msg.getSessionid());
        GatePeer gatePeer = ClientManager.Instance.find_objr( peerid );

        if( gatePeer != null)
        {
            gatePeer.IsValid = false;
            gatePeer.discannect();
        }
        else
        {
            GateServer.Instance.push_id(peerid);
        }

        return true;
    }
}
