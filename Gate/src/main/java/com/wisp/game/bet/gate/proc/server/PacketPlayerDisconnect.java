package com.wisp.game.bet.gate.proc.server;

import client2gate_protocols.Client2GateProtocol;
import com.wisp.game.bet.gate.unit.ClientManager;
import com.wisp.game.bet.gate.unit.GatePeer;
import com.wisp.game.bet.gate.unit.ServerPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import com.wisp.game.bet.share.utils.SessionHelper;
import server_protocols.ServerProtocol;

@IRequest
public class PacketPlayerDisconnect extends DefaultRequestMessage<ServerProtocol.packet_player_disconnect, ServerPeer> {

    @Override
    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_player_disconnect msg) {
        GatePeer gatePeer = ClientManager.Instance.find_objr(SessionHelper.get_peerid(msg.getSessionid()));

        if(  gatePeer == null)
        {
            return true;
        }

        if( msg.getShutdown() )
        {
            Client2GateProtocol.packetg2c_shutdown.Builder builder = Client2GateProtocol.packetg2c_shutdown.newBuilder();
            gatePeer.send_msg(builder.build());
        }
        else
        {
            gatePeer.logic_id = 0;
            gatePeer.discannect();
        }

        return true;
    }
}
