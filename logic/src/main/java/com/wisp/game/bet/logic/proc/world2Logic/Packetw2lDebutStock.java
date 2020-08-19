package com.wisp.game.bet.logic.proc.world2Logic;

import com.wisp.game.bet.logic.sshare.IGameEngine;
import com.wisp.game.bet.logic.unit.ServerPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import logic2world_protocols.Logic2WorldProtocol;

import javax.annotation.Resource;

@IRequest
public class Packetw2lDebutStock extends DefaultRequestMessage<Logic2WorldProtocol.packetw2l_deduct_stock, ServerPeer> {
   @Resource
    private IGameEngine gameEngine;
    @Override
    public boolean packet_process(ServerPeer peer, Logic2WorldProtocol.packetw2l_deduct_stock msg) {
        gameEngine.deduct_stock(msg.getRoomId(),msg.getGold());
        return true;
    }
}
