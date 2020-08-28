package com.wisp.game.bet.logic.proc.server;

import com.wisp.game.bet.logic.unit.ServerPeer;
import com.wisp.game.bet.share.component.TimeHelper;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerProtocol;

@IRequest
public class PacketServerRegisterResult extends DefaultRequestMessage<ServerProtocol.packet_server_register_result, ServerPeer> {
    @Override
    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_server_register_result msg) {
        TimeHelper.Instance.set_base_time( Integer.valueOf( String.valueOf(msg.getServerTime()) )  );
        peer.set_remote_id(peer.get_remote_port());

        return true;
    }
}
