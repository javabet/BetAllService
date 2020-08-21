package com.wisp.game.bet.games.baccarat.proc;

import com.wisp.game.bet.games.baccarat.logic.LogicPlayer;
import com.wisp.game.bet.games.baccarat.logic.LogicRoom;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.unit.LogicPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.RequestMessageFromGate;
import game_baccarat_protocols.GameBaccaratDef;
import game_baccarat_protocols.GameBaccaratProtocol;
import msg_type_def.MsgTypeDef;


//续压
@IRequest(10005)
public class Packetc2lRepeatBet extends RequestMessageFromGate<GameBaccaratProtocol.packetc2l_repeat_bet, LogicPeer, GamePlayer> {
    @Override
    public boolean packet_process(LogicPeer peer, GamePlayer player, GameBaccaratProtocol.packetc2l_repeat_bet msg) {
        LogicPlayer logicPlayer = (LogicPlayer) player.getPhandler();

        GameBaccaratProtocol.packetl2c_repeat_bet_result.Builder builder  = GameBaccaratProtocol.packetl2c_repeat_bet_result.newBuilder();
        LogicRoom logicRoom = logicPlayer.get_room();

        if( logicRoom == null )
        {
            builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_no_can_bet);
        }
        else
        {
            if( logicRoom.get_room_state() == GameBaccaratDef.e_game_state.e_state_game_bet)
            {
                builder.setResult(logicPlayer.repeat_bet());
            }
            else
            {
                builder.setResult( MsgTypeDef.e_msg_result_def.e_rmt_no_can_bet );
            }
        }

        player.send_msg_to_client(builder);

        return true;
    }
}
