package com.wisp.game.bet.gate.proc.server;

import com.wisp.game.bet.gate.unit.GateServer;
import com.wisp.game.bet.gate.unit.ServerPeer;
import com.wisp.game.share.netty.IRequest;
import com.wisp.game.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerProtocol;

@IRequest
public class PacketServerDown extends DefaultRequestMessage<ServerProtocol.packet_serverdown, ServerPeer> {

    @Override
    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_serverdown msg) {
        GateServer.Instance.close();
        return true;
    }
}
