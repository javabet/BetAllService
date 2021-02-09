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

@IRequest(10001)
public class Packetc2lGetSceneInfo extends RequestMessageFromGate<GameGuanyunProtocol.packetc2l_get_scene_info, LogicPeer, GamePlayer>
{
    @Autowired
    private LogicLobby logicLobby;

    @Override
    public boolean packet_process(LogicPeer peer, GamePlayer player, GameGuanyunProtocol.packetc2l_get_scene_info msg)
    {
        GameGuanyunProtocol.packetl2c_get_scene_info_result.Builder builder = GameGuanyunProtocol.packetl2c_get_scene_info_result.newBuilder();
        LogicPlayer logicPlayer = (LogicPlayer)player.getPhandler();
        if( logicPlayer.getRoomId() == 0 )
        {
            builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_fail);
            player.send_msg_to_client(builder);
            return true;
        }

        LogicTable logicTable = logicLobby.getLogicByRoomId(logicPlayer.getRoomId());
        if( logicTable == null )
        {
            builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_fail);
            player.send_msg_to_client(builder);
            return true;
        }
        builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_success);
        logicTable.get_scene_info_result(builder,logicPlayer.getSeatIndex() );
        player.send_msg_to_client(builder);

        return true;
    }
}
