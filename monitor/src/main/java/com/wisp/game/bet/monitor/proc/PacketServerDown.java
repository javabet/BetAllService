package com.wisp.game.bet.monitor.proc;

import com.wisp.game.bet.monitor.unit.MonitorPeer;
import com.wisp.game.bet.monitor.unit.MonitorServer;
import com.wisp.game.share.netty.IRequest;
import com.wisp.game.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerProtocol;

@IRequest
public class PacketServerDown extends DefaultRequestMessage<ServerProtocol.packet_serverdown, MonitorPeer> {

    public boolean packet_process( MonitorPeer peer,ServerProtocol.packet_serverdown message) {

        MonitorServer.Instance.close();

        return true;
    }
}
