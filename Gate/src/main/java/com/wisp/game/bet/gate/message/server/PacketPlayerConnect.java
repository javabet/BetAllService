package com.wisp.game.bet.gate.message.server;

import com.wisp.game.bet.gate.services.BackstageManager;
import com.wisp.game.bet.gate.services.ClientManager;
import com.wisp.game.bet.gate.services.GatePeer;
import com.wisp.game.bet.gate.services.ServerPeer;
import com.wisp.game.share.netty.IRequest;
import com.wisp.game.share.netty.PacketManager.DefaultRequestMessage;
import com.wisp.game.share.utils.SessionHelper;
import server_protocols.ServerProtocol;

@IRequest
public class PacketPlayerConnect extends DefaultRequestMessage<ServerProtocol.packet_player_connect, ServerPeer> {

    @Override
    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_player_connect msg) {
        GatePeer gatePeer = ClientManager.Instance.find_objr(SessionHelper.get_peerid(msg.getSessionid()));

        if(gatePeer == null)
        {
            return true;
        }

        gatePeer.logic_id = msg.getLogicid();

        ServerPeer serverPeer = BackstageManager.Instance.get_server_byid( msg.getLogicid() );
        if( serverPeer != null )
        {
            ServerProtocol.packet_gate_setlogic_ok.Builder builder = ServerProtocol.packet_gate_setlogic_ok.newBuilder();
            builder.setSessionid(msg.getSessionid());
            serverPeer.send_msg(builder.build());
        }


        return true;
    }
}
