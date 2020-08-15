package com.wisp.game.bet.world.proc.world;


import com.wisp.game.bet.world.unit.ServerPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerProtocol;

@IRequest
public class PacketServerConnectResult extends DefaultRequestMessage<ServerProtocol.packet_server_connect_result, ServerPeer> {
    @Override
    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_server_connect_result msg) {
        //peer.set_remote_id(peer.get_remote_port());
        ServerProtocol.packet_server_connect_result.newBuilder();

        return true;
    }
}
