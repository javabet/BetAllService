package com.wisp.game.bet.games.baccarat.proc;

import com.wisp.game.bet.games.baccarat.logic.LogicPlayer;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.unit.LogicPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.RequestMessageFromGate;
import game_baccarat_protocols.GameBaccaratProtocol;
import msg_type_def.MsgTypeDef;

import java.util.List;

//离开桌子
@IRequest
public class Pacetc2lLeaveRoom extends RequestMessageFromGate<GameBaccaratProtocol.packetc2l_leave_room, LogicPeer, GamePlayer> {


    @Override
    public boolean packet_process(LogicPeer peer, GamePlayer player, GameBaccaratProtocol.packetc2l_leave_room msg) {

        LogicPlayer logicPlayer = (LogicPlayer)player.getPhandler();

        GameBaccaratProtocol.packetl2c_leave_room_result.Builder builder = GameBaccaratProtocol.packetl2c_leave_room_result.newBuilder();

        List<Integer> temp_bet = logicPlayer.get_bet_list();

        boolean haveBet = false;

        for(int i = 0; i < 5; i ++ )
        {
            if( temp_bet.get(i) > 0 )
            {
                haveBet = true;
                break;
            }
        }

        if( logicPlayer.get_is_banker() )
        {
            builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_can_not_leave);
        }
        else
        {
            if( haveBet )
            {
                builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_can_not_leave);
            }
            else
            {
                logicPlayer.leave_room();
                builder.setResult( MsgTypeDef.e_msg_result_def.e_rmt_success );
                builder.setPlayerGold(logicPlayer.get_gold());
            }
        }


        player.send_msg_to_client(builder);

        return true;
    }
}
