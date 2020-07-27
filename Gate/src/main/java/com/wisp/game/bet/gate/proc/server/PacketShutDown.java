package com.wisp.game.bet.gate.proc.server;

import com.wisp.game.bet.gate.unit.ClientManager;
import com.wisp.game.bet.gate.unit.ServerPeer;
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
