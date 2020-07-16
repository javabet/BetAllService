package com.wisp.game.bet.gate.message.server;

import com.wisp.game.bet.gate.services.ClientManager;
import com.wisp.game.bet.gate.services.ServerPeer;
import com.wisp.game.share.netty.IRequest;
import com.wisp.game.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerProtocol;

@IRequest
public class PacketShutDown extends DefaultRequestMessage<ServerProtocol.packet_shutdown, ServerPeer> {

    @Override
    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_shutdown msg) {

        ClientManager.Instance.prepare_shutdown(msg.getGameid());

        return true;
    }
}
