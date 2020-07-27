package com.wisp.game.bet.world.proc.server;

import com.wisp.game.bet.world.unit.ServerPeer;
import com.wisp.game.share.netty.IRequest;
import com.wisp.game.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerProtocol;

@IRequest
public class PacketGetIpResult extends DefaultRequestMessage<ServerProtocol.packet_get_ip_result, ServerPeer> {


    @Override
    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_get_ip_result msg) {

        //do nothing
        return true;
    }
}
