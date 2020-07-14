package com.wisp.game.bet.monitor.proc;

import com.wisp.game.bet.monitor.unit.MonitorPeer;
import com.wisp.game.bet.monitor.unit.MonitorServer;
import com.wisp.game.share.netty.IRequest;
import com.wisp.game.share.netty.PacketManager.IRequestMessage;
import server_protocols.ServerProtocol;

@IRequest
public class PacketServerDown implements IRequestMessage<ServerProtocol.packet_serverdown, MonitorPeer> {

    public boolean process(ServerProtocol.packet_serverdown message, MonitorPeer peer) {

        MonitorServer.Instance.close();

        return true;
    }
}
