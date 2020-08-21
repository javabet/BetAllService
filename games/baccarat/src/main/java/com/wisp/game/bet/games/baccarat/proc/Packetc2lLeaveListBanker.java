package com.wisp.game.bet.games.baccarat.proc;

import com.wisp.game.bet.games.baccarat.logic.LogicPlayer;
import com.wisp.game.bet.games.baccarat.logic.LogicRoom;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.unit.LogicPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.RequestMessageFromGate;
import game_baccarat_protocols.GameBaccaratProtocol;
import msg_type_def.MsgTypeDef;

//离开上庄列表
@IRequest(10017)
public class Packetc2lLeaveListBanker extends RequestMessageFromGate<GameBaccaratProtocol.packetc2l_leave_list_banker, LogicPeer, GamePlayer> {
    @Override
    public boolean packet_process(LogicPeer peer, GamePlayer player, GameBaccaratProtocol.packetc2l_leave_list_banker msg) {

        GameBaccaratProtocol.packetl2c_leave_list_banker_result.Builder builder = GameBaccaratProtocol.packetl2c_leave_list_banker_result.newBuilder();

        LogicPlayer logicPlayer = (LogicPlayer) player.getPhandler();
        LogicRoom logicRoom = logicPlayer.get_room();

        if( logicRoom == null )
        {
             builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_fail);
        }
        else
        {
            builder.setResult( logicRoom.remove_banker_list(logicPlayer.get_pid()) );
        }

        player.send_msg_to_client(builder);

        return true;
    }
}
