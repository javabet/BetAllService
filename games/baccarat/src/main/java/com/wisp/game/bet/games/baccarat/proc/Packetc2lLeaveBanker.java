package com.wisp.game.bet.games.baccarat.proc;

import com.wisp.game.bet.games.baccarat.logic.LogicPlayer;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.unit.LogicPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.RequestMessageFromGate;
import game_baccarat_protocols.GameBaccaratProtocol;


//下庄
@IRequest
public class Packetc2lLeaveBanker extends RequestMessageFromGate<GameBaccaratProtocol.packetc2l_leave_banker, LogicPeer, GamePlayer> {
    @Override
    public boolean packet_process(LogicPeer peer, GamePlayer player, GameBaccaratProtocol.packetc2l_leave_banker msg) {

        LogicPlayer logicPlayer = (LogicPlayer)player.getPhandler();

        GameBaccaratProtocol.packetl2c_leave_banker_result.Builder builder = GameBaccaratProtocol.packetl2c_leave_banker_result.newBuilder();
        builder.setResult(logicPlayer.leave_banker());

        player.send_msg_to_client(builder);

        return true;
    }
}
