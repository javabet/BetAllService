package com.wisp.game.bet.gate.proc.server;

import com.wisp.game.bet.gate.unit.BackstageManager;
import com.wisp.game.bet.gate.unit.ServerPeer;
import com.wisp.game.share.netty.IRequest;
import com.wisp.game.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

import java.util.concurrent.ConcurrentHashMap;

@IRequest
public class PacketOtherServerDisconnect extends DefaultRequestMessage<ServerProtocol.packet_other_server_disconnect, ServerPeer> {

    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_other_server_disconnect msg) {

       ConcurrentHashMap<Integer, ServerBase.server_info> sInfoMap =  BackstageManager.Instance.getSInfoMap();
       if( sInfoMap.containsKey(msg.getServerId()) )
       {
           sInfoMap.remove(msg.getServerId());
       }

       logger.info("other_server_disconnect id:" + msg.getServerId());

        return true;
    }
}
