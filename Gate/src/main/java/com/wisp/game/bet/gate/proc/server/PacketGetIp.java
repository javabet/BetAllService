package com.wisp.game.bet.gate.proc.server;

import com.wisp.game.bet.gate.unit.ClientManager;
import com.wisp.game.bet.gate.unit.GatePeer;
import com.wisp.game.bet.gate.unit.ServerPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import com.wisp.game.bet.share.utils.SessionHelper;
import server_protocols.ServerProtocol;

@IRequest
public class PacketGetIp extends DefaultRequestMessage<ServerProtocol.packet_get_ip, ServerPeer> {

    @Override
    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_get_ip msg) {

        GatePeer gatePeer = ClientManager.Instance.find_objr(SessionHelper.get_peerid(msg.getSessionid()));
        if( gatePeer == null)
        {
            return true;
        }

        ServerProtocol.packet_get_ip_result.Builder builder = ServerProtocol.packet_get_ip_result.newBuilder();
        builder.setSessionid(msg.getSessionid());
        builder.setIp(gatePeer.get_remote_ip());
        builder.setPort(gatePeer.get_remote_id());
        peer.send_msg(builder.build());

        return true;
    }
}
