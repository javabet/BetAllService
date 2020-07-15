package com.wisp.game.bet.monitor.proc;

import com.google.protobuf.Message;
import com.wisp.game.bet.monitor.unit.MonitorPeer;
import com.wisp.game.share.netty.IRequest;
import com.wisp.game.share.netty.PacketManager.DefaultRequestMessage;
import com.wisp.game.share.netty.PacketManager.IRequestMessage;
import com.wisp.game.share.netty.PeerTcp;
import server_protocols.ServerProtocol;

@IRequest
public class PacketHeartBeat extends DefaultRequestMessage<ServerProtocol.packet_heartbeat,MonitorPeer>{


    @Override
    public boolean packet_process(MonitorPeer peer, ServerProtocol.packet_heartbeat msg) {
        peer.reset_time();
        return true;
    }
}
