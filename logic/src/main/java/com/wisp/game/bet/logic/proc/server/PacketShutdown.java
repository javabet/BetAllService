package com.wisp.game.bet.logic.proc.server;

import com.wisp.game.bet.logic.gameMgr.GameManager;
import com.wisp.game.bet.logic.unit.ServerPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerProtocol;

@IRequest
public class PacketShutdown extends DefaultRequestMessage<ServerProtocol.packet_shutdown, ServerPeer> {
    @Override
    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_shutdown msg) {

        GameManager.Instance.prepare_shutdown(msg.getGameid());

        return true;
    }
}
