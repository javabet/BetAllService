package com.wisp.game.bet.monitor.proc;

import com.wisp.game.bet.monitor.unit.MonitorPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import org.springframework.beans.factory.InitializingBean;
import server_protocols.ServerProtocol;

@IRequest
public class PacketHeartBeat extends DefaultRequestMessage<ServerProtocol.packet_heartbeat, MonitorPeer> implements InitializingBean {



    @Override
    public boolean packet_process(MonitorPeer peer, ServerProtocol.packet_heartbeat msg) {
        peer.reset_time();
        return true;
    }

     public void afterPropertiesSet() throws Exception
    {
        System.out.printf("..................\n");
    }
}
