package com.wisp.game.bet.world.proc.server;

import com.wisp.game.bet.world.unit.BackstageManager;
import com.wisp.game.bet.world.unit.ServerPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

import java.util.concurrent.ConcurrentHashMap;

@IRequest
public class PacketOtherServerDisconnect extends DefaultRequestMessage<ServerProtocol.packet_other_server_disconnect, ServerPeer> {


    @Override
    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_other_server_disconnect msg) {
        ConcurrentHashMap<Integer, ServerBase.server_info> sinfoMap =  BackstageManager.Instance.SInfoMap;
        sinfoMap.remove(msg.getServerId());

        return true;
    }
}
