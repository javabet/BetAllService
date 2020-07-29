package com.wisp.game.bet.gate.proc.server;

import com.wisp.game.bet.gate.unit.ClientManager;
import com.wisp.game.bet.gate.unit.GatePeer;
import com.wisp.game.bet.gate.unit.GateServer;
import com.wisp.game.bet.gate.unit.ServerPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import com.wisp.game.bet.share.utils.SessionHelper;
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
