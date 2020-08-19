package com.wisp.game.bet.logic.proc.world2Logic;

import com.wisp.game.bet.logic.sshare.IGameEngine;
import com.wisp.game.bet.logic.unit.ServerPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import logic2world_protocols.Logic2WorldProtocol;

import javax.annotation.Resource;

@IRequest
public class packetw2lSetRoom extends DefaultRequestMessage<Logic2WorldProtocol.packetw2l_set_room, ServerPeer> {
    @Resource
    private IGameEngine gameEngine;

    @Override
    public boolean packet_process(ServerPeer peer, Logic2WorldProtocol.packetw2l_set_room msg) {
        gameEngine.set_room(msg.getAgentId(),msg.getRoomId());
        return true;
    }
}
