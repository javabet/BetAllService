package com.wisp.game.bet.games.baccarat.proc;

import com.wisp.game.bet.games.baccarat.logic.LogicPlayer;
import com.wisp.game.bet.games.baccarat.logic.LogicRoom;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.unit.LogicPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.RequestMessageFromGate;
import game_baccarat_protocols.GameBaccaratProtocol;
import msg_type_def.MsgTypeDef;

//上庄
@IRequest(10010)
public class Packetc2lAskForBanker extends RequestMessageFromGate<GameBaccaratProtocol.packetc2l_ask_for_banker, LogicPeer, GamePlayer> {
    @Override
    public boolean packet_process(LogicPeer peer, GamePlayer player, GameBaccaratProtocol.packetc2l_ask_for_banker msg) {
        LogicPlayer logicPlayer = (LogicPlayer)player.getPhandler();
        GameBaccaratProtocol.packetl2c_ask_for_banker_result.Builder builder = GameBaccaratProtocol.packetl2c_ask_for_banker_result.newBuilder();
        LogicRoom logicRoom = logicPlayer.get_room();

        if( logicRoom != null )
        {
            builder.setResult( logicRoom.add_banker_list(logicPlayer.get_pid()) );
        }
        else
        {
            builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_fail);
        }

        player.send_msg_to_client(builder);

        return true;
    }
}
