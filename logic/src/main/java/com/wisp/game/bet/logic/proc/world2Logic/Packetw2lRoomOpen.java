package com.wisp.game.bet.logic.proc.world2Logic;

import com.wisp.game.bet.logic.sshare.IGameEngine;
import com.wisp.game.bet.logic.unit.ServerPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import logic2world_protocols.Logic2WorldProtocol;

import javax.annotation.Resource;

@IRequest
public class Packetw2lRoomOpen extends DefaultRequestMessage<Logic2WorldProtocol.packetw2l_room_open, ServerPeer> {

    @Resource
    private IGameEngine gameEngine;

    @Override
    public boolean packet_process(ServerPeer peer, Logic2WorldProtocol.packetw2l_room_open msg) {
        gameEngine.open_room(msg.getAgentId(),msg.getOpen());
        return true;
    }
}
