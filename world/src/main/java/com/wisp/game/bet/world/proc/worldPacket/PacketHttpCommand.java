package com.wisp.game.bet.world.proc.worldPacket;

import com.wisp.game.bet.world.unit.WorldPeer;
import com.wisp.game.share.netty.IRequest;
import com.wisp.game.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerProtocol;

@IRequest
public class PacketHttpCommand extends DefaultRequestMessage<ServerProtocol.packet_http_command, WorldPeer> {


    @Override
    public boolean packet_process(WorldPeer peer, ServerProtocol.packet_http_command msg) {
        return true;
    }
}
