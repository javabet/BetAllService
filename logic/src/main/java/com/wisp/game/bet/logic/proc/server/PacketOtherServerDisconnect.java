package com.wisp.game.bet.logic.proc.server;

import com.wisp.game.bet.logic.unit.BackstageManager;
import com.wisp.game.bet.logic.unit.ServerPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

import java.util.concurrent.ConcurrentHashMap;

@IRequest
public class PacketOtherServerDisconnect extends DefaultRequestMessage<ServerProtocol.packet_other_server_disconnect, ServerPeer> {

    @Override
    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_other_server_disconnect msg) {
        ConcurrentHashMap<Integer, ServerBase.server_info> simap = BackstageManager.Instance.SInfoMap;

        simap.remove(msg.getServerId());

        logger.info("other_server_disconnect id:" + msg.getServerId());

        return true;
    }
}
