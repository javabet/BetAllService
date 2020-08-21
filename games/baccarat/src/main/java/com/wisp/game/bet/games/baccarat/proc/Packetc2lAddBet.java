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


//下注
@IRequest(10004)
public class Packetc2lAddBet extends RequestMessageFromGate<GameBaccaratProtocol.packetc2l_add_bet, LogicPeer, GamePlayer> {
    @Override
    public boolean packet_process(LogicPeer peer, GamePlayer player, GameBaccaratProtocol.packetc2l_add_bet msg) {
        LogicPlayer logicPlayer = (LogicPlayer) player.getPhandler();

        GameBaccaratProtocol.packetl2c_add_bet_result.Builder builder = GameBaccaratProtocol.packetl2c_add_bet_result.newBuilder();
        LogicRoom logicRoom = logicPlayer.get_room();

        if(logicRoom != null)
        {
            if( logicRoom.get_room_state() == GameBaccaratDef.e_game_state.e_state_game_bet)
            {
                int count = msg.getBetCount();
                MsgTypeDef.e_msg_result_def temp_result =  logicPlayer.add_bet(msg.getBetIndex(),count);
                builder.setResult(temp_result);

                if( temp_result == MsgTypeDef.e_msg_result_def.e_rmt_success )
                {
                    builder.setBetIndex(msg.getBetIndex());
                    builder.setBetCount(count);
                }
            }
            else
            {
                builder.setResult( MsgTypeDef.e_msg_result_def.e_rmt_no_can_bet );
            }
        }
        else
        {
            builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_no_can_bet);
        }

        player.send_msg_to_client(builder);

        return true;
    }
}
