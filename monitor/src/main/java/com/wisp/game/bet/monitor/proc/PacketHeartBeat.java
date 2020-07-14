package com.wisp.game.bet.monitor.proc;

import com.wisp.game.bet.monitor.unit.MonitorPeer;
import com.wisp.game.share.netty.IRequest;
import com.wisp.game.share.netty.PacketManager.IRequestMessage;
import server_protocols.ServerProtocol;

@IRequest
public class PacketHeartBeat implements IRequestMessage<ServerProtocol.packet_heartbeat, MonitorPeer> {

    public boolean process(ServerProtocol.packet_heartbeat message, MonitorPeer peer) {

        peer.reset_time();

        return false;
    }
}
