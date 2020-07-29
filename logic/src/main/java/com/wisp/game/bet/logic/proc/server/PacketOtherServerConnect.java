package com.wisp.game.bet.logic.proc.server;

import com.wisp.game.bet.logic.unit.BackstageManager;
import com.wisp.game.bet.logic.unit.ServerPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

import java.util.concurrent.ConcurrentHashMap;

@IRequest
public class PacketOtherServerConnect extends DefaultRequestMessage<ServerProtocol.packet_other_server_connect, ServerPeer> {


    @Override
    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_other_server_connect msg) {
        ConcurrentHashMap<Integer, ServerBase.server_info> simap = BackstageManager.Instance.SInfoMap;

        if( !simap.containsKey(msg.getSinfo().getServerPort()) )
        {
            simap.put(msg.getSinfo().getServerPort(),msg.getSinfo());
        }

        logger.info("other_server_connect id:" + msg.getSinfo().getServerPort() + " type:" + msg.getSinfo().getServerType() );

        if( msg.getSinfo().getServerType() == ServerBase.e_server_type.e_st_world )
        {
            BackstageManager.Instance.connect_world();
        }

        return true;
    }
}
