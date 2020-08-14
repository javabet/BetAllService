package com.wisp.game.bet.logic.proc.logic;

import com.wisp.game.bet.logic.gameMgr.GamePlayerMgr;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.unit.LogicPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerProtocol;

@IRequest
public class PacketGateSetlogicOk extends DefaultRequestMessage<ServerProtocol.packet_gate_setlogic_ok, LogicPeer> {
    @Override
    public boolean packet_process(LogicPeer peer, ServerProtocol.packet_gate_setlogic_ok msg) {
        GamePlayer gamePlayer = GamePlayerMgr.Instance.find_player(msg.getSessionid());
        if( gamePlayer == null )
        {
            return true;
        }

        gamePlayer.GatePeer = peer;

        return true;
    }
}
