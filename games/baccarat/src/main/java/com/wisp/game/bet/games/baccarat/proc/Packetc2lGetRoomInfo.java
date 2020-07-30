package com.wisp.game.bet.games.baccarat.proc;

import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.unit.LogicPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import com.wisp.game.bet.share.netty.PacketManager.RequestMessageFromGate;
import game_baccarat_protocols.GameBaccaratProtocol;

@IRequest
public class Packetc2lGetRoomInfo extends RequestMessageFromGate<GameBaccaratProtocol.packetc2l_get_room_info, LogicPeer,GamePlayer> {
    @Override
    public boolean packet_process(LogicPeer peer, GamePlayer player, GameBaccaratProtocol.packetc2l_get_room_info msg) {
        return true;
    }
}
