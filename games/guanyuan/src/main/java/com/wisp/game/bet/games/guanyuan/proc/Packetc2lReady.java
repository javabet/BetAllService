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
import org.springframework.stereotype.Component;


@IRequest(10005)
public class Packetc2lReady extends RequestMessageFromGate<GameGuanyunProtocol.packetc2l_ready, LogicPeer, GamePlayer> {

    @Autowired
    private LogicLobby logicLobby;

    @Override
    public boolean packet_process(LogicPeer peer, GamePlayer player, GameGuanyunProtocol.packetc2l_ready msg) {

        LogicPlayer logicPlayer = (LogicPlayer)player.getPhandler();
        LogicTable logicTable = logicLobby.getLogicByRoomId(logicPlayer.getRoomId());

        GameGuanyunProtocol.packetl2c_action_result.Builder builder = GameGuanyunProtocol.packetl2c_action_result.newBuilder();

        if( logicTable == null )
        {
            builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_fail);      //房间不存在
            return true;
        }

        logicTable.ready(msg.getSeatPos(),msg.getReady());

        return true;
    }
}
