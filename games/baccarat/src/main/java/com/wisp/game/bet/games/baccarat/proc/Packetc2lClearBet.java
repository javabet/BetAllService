package com.wisp.game.bet.games.baccarat.proc;

import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.unit.LogicPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.RequestMessageFromGate;
import game_baccarat_protocols.GameBaccaratProtocol;
import msg_type_def.MsgTypeDef;

@IRequest(10006)
public class Packetc2lClearBet extends RequestMessageFromGate<GameBaccaratProtocol.packetc2l_clear_bet, LogicPeer, GamePlayer> {
    @Override
    public boolean packet_process(LogicPeer peer, GamePlayer player, GameBaccaratProtocol.packetc2l_clear_bet msg) {

        GameBaccaratProtocol.packetl2c_clear_bet_result.Builder builder = GameBaccaratProtocol.packetl2c_clear_bet_result.newBuilder();

        builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_no_can_bet);

        player.send_msg_to_client(builder);

        return true;
    }
}
