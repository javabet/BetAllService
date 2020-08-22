package com.wisp.game.bet.games.baccarat.proc;

import com.wisp.game.bet.games.baccarat.logic.LogicPlayer;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.unit.LogicPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.RequestMessageFromGate;
import game_baccarat_protocols.GameBaccaratProtocol;

//检测协议
@IRequest(10008)
public class Packetc2lCheckState extends RequestMessageFromGate<GameBaccaratProtocol.packetc2l_check_state, LogicPeer, GamePlayer> {
    @Override
    public boolean packet_process(LogicPeer peer, GamePlayer player, GameBaccaratProtocol.packetc2l_check_state msg) {
        LogicPlayer logicPlayer = (LogicPlayer)player.getPhandler();
        GameBaccaratProtocol.packetl2c_check_state_result.Builder builder =  GameBaccaratProtocol.packetl2c_check_state_result.newBuilder();

        if( logicPlayer != null )
        {
            builder.setIsIntable( logicPlayer.get_room() != null );
        }
        else
        {
            /**
            if( logicPlayer.get_room() != null && logicPlayer.get_room().is_ex_room() &&
                    logicPlayer.get_gold() != logicPlayer.get_room().get_free_gold() )
            {
                builder.setIsIntable(false);
            }

            else
            {
                builder.setIsIntable( logicPlayer.get_room() != null );
            }
             **/
            builder.setIsIntable( false );
        }

        player.send_msg_to_client(builder);

        return true;
    }
}
