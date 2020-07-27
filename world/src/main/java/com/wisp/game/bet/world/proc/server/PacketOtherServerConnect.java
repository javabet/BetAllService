package com.wisp.game.bet.world.proc.server;

import com.wisp.game.bet.world.unit.BackstageManager;
import com.wisp.game.bet.world.unit.ServerPeer;
import com.wisp.game.share.netty.IRequest;
import com.wisp.game.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

import java.util.concurrent.ConcurrentHashMap;

@IRequest
public class PacketOtherServerConnect extends DefaultRequestMessage<ServerProtocol.packet_other_server_connect, ServerPeer> {


    @Override
    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_other_server_connect msg) {
        ConcurrentHashMap<Integer, ServerBase.server_info> sinfoMap =  BackstageManager.Instance.SInfoMap;

        if( sinfoMap.containsKey(msg.getSinfo().getServerPort()) )
        {
            logger.error("other_server_connect error id:" + msg.getSinfo().getServerPort() + " type:" + msg.getSinfo().getServerType() );
            return false;
        }

        sinfoMap.put(msg.getSinfo().getServerPort(),msg.getSinfo());


        return true;
    }
}
