package com.wisp.game.bet.games.guanyuan.proc;

import com.wisp.game.bet.games.guanyuan.logic.LogicLobby;
import com.wisp.game.bet.games.guanyuan.logic.LogicPlayer;
import com.wisp.game.bet.games.guanyuan.logic.LogicTable;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.unit.LogicPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.RequestMessageFromGate;
import game_guanyuan_protocols.GameGuanyunProtocol;
import msg_type_def.MsgTypeDef;
import org.springframework.beans.factory.annotation.Autowired;

@IRequest(10003)
public class Packetc2lLeaveRoom extends RequestMessageFromGate<GameGuanyunProtocol.packetc2l_leave_room, LogicPeer, GamePlayer> {

    @Autowired
    private LogicLobby logicLobby;

    @Override
    public boolean packet_process(LogicPeer peer, GamePlayer player, GameGuanyunProtocol.packetc2l_leave_room msg) {
        LogicPlayer logicPlayer = (LogicPlayer)player.getPhandler();
        LogicTable logicTable = logicLobby.getLogicByRoomId(logicPlayer.getRoomId());

        GameGuanyunProtocol.packetl2c_action_result.Builder builder = GameGuanyunProtocol.packetl2c_action_result.newBuilder();

        if( logicTable == null )
        {
            builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_fail);      //房间不存在
            logicPlayer.send_msg_to_client(builder);
            return true;
        }

        if( logicTable.getOwnUid() == player.get_playerid() && logicTable.getPlayerMap().size() > 1)
        {
            builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_fail);      //房主在房间有其它时，不能直接退出，只能请求解散房间
            logicPlayer.send_msg_to_client(builder);
            return true;
        }

        if( logicTable.getGameSttus() != LogicTable.GameSttus.STATUS_INIT )
        {
            builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_fail);      //只能在准备时间退出
            logicPlayer.send_msg_to_client(builder);
            return true;
        }

        logicTable.removePlayer(logicPlayer);

        if( logicTable.getOwnUid() == player.get_playerid() )
        {
            logicLobby.removeRoomByRoomId(logicTable.getRoomId());
        }

        return true;
    }
}
