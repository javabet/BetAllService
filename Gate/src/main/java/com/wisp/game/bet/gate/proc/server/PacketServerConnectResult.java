package com.wisp.game.bet.gate.proc.server;

import com.wisp.game.bet.gate.unit.ServerPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerProtocol;

@IRequest
public class PacketServerConnectResult extends DefaultRequestMessage<ServerProtocol.packet_server_connect_result, ServerPeer> {

    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_server_connect_result msg) {

        peer.set_remote_id(peer.get_remote_port());
        peer.regedit_result(true);

        logger.info("packet_server_connect_result ok id:" + peer.get_remote_id() + " type:" + peer.get_remote_type() + "\n" );
        return true;
    }
}
