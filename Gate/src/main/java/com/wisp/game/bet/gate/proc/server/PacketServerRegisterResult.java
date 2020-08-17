package com.wisp.game.bet.gate.proc.server;

import com.wisp.game.bet.gate.unit.BackstageManager;
import com.wisp.game.bet.gate.unit.ServerPeer;
import com.wisp.game.bet.share.component.TimeHelper;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerProtocol;

@IRequest
public class PacketServerRegisterResult extends DefaultRequestMessage<ServerProtocol.packet_server_register_result, ServerPeer> {


    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_server_register_result msg) {

        TimeHelper.Instance.set_base_time( Integer.valueOf(String.valueOf( msg.getServerTime())));
        //peer.set_remote_id(peer.get_remote_port());
        BackstageManager.Instance.regedit_server(peer);

        logger.info(" packet_server_register_result success:");
        return true;
    }
}
