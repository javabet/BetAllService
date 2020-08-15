package com.wisp.game.bet.world.proc.server;

import com.wisp.game.bet.world.unit.BackstageManager;
import com.wisp.game.bet.world.unit.ServerPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

import java.util.concurrent.ConcurrentHashMap;

//monitor 广播给所有的服务器某个服务器的状态
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

        logger.info("packet_other_server_connect port:" + msg.getSinfo().getServerPort() + " type:" + msg.getSinfo().getServerType());

        return true;
    }
}
