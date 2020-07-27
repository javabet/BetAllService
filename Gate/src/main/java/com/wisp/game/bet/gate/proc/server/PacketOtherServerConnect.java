package com.wisp.game.bet.gate.proc.server;

import com.wisp.game.bet.gate.unit.BackstageManager;
import com.wisp.game.bet.gate.unit.ServerPeer;
import com.wisp.game.share.netty.IRequest;
import com.wisp.game.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

import java.util.concurrent.ConcurrentHashMap;

@IRequest
public class PacketOtherServerConnect extends DefaultRequestMessage<ServerProtocol.packet_other_server_connect, ServerPeer> {

    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_other_server_connect msg) {
        ConcurrentHashMap<Integer, ServerBase.server_info> sInfoMap =  BackstageManager.Instance.getSInfoMap();

        if( !sInfoMap.containsKey(msg.getSinfo().getServerPort()) )
        {
            sInfoMap.put(msg.getSinfo().getServerPort(),msg.getSinfo());
        }

        BackstageManager.Instance.check_servers();

        logger.info("other_server_connect id:" + msg.getSinfo().getServerPort() + " type:" + msg.getSinfo().getServerType()) ;
        return true;
    }
}
