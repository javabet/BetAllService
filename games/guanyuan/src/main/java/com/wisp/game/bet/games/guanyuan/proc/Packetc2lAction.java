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

//玩家操作类型
@IRequest(10004)
public class Packetc2lAction  extends RequestMessageFromGate<GameGuanyunProtocol.packetc2l_action, LogicPeer, GamePlayer> {

    @Autowired
    private LogicLobby logicLobby;

    @Override
    public boolean packet_process(LogicPeer peer, GamePlayer player, GameGuanyunProtocol.packetc2l_action msg) {

        LogicPlayer logicPlayer = (LogicPlayer)player.getPhandler();
        LogicTable logicTable = logicLobby.getLogicByRoomId(logicPlayer.getRoomId());

        GameGuanyunProtocol.packetl2c_action_result.Builder builder = GameGuanyunProtocol.packetl2c_action_result.newBuilder();

        if( logicTable == null )
        {
            builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_fail);      //房间不存在
            return true;
        }


        switch (msg.getActionType().getNumber())
        {
            case  GameGuanyunProtocol.e_action_type.e_action_skip_VALUE:
                    logicTable.skip(logicPlayer.getSeatIndex());
                break;
            case GameGuanyunProtocol.e_action_type.e_action_peng_VALUE:
                    logicTable.peng(logicPlayer.getSeatIndex());
                break;
            case GameGuanyunProtocol.e_action_type.e_action_out_card_VALUE:
                    logicTable.outCard(logicPlayer.getSeatIndex(),msg.getCards(0),false);
                break;
            case GameGuanyunProtocol.e_action_type.e_action_gang_VALUE:
                    logicTable.gang(logicPlayer.getSeatIndex(),msg.getCards(0));
                break;
            case GameGuanyunProtocol.e_action_type.e_action_hu_VALUE:
                    logicTable.hu(logicPlayer.getSeatIndex());
                break;
            case GameGuanyunProtocol.e_action_type.e_action_ting_VALUE:
                    logicTable.outCard(logicPlayer.getSeatIndex(),msg.getCards(0),true);
                break;

        }

        return true;
    }
}
