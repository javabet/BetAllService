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

@IRequest(10010)
public class Packetc2lCheckState extends RequestMessageFromGate<GameGuanyunProtocol.packetc2l_check_state, LogicPeer, GamePlayer>
{
    @Autowired
    private LogicLobby logicLobby;
    @Override
    public boolean packet_process(LogicPeer peer, GamePlayer player, GameGuanyunProtocol.packetc2l_check_state msg)
    {
        GameGuanyunProtocol.packetl2c_check_state_result.Builder builder = GameGuanyunProtocol.packetl2c_check_state_result.newBuilder();
        LogicPlayer  logicPlayer = (LogicPlayer)player.getPhandler();
        if( logicPlayer.getRoomId() == 0 )
        {
            builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_fail.getNumber());
        }
        else
        {
            LogicTable logicTable = logicLobby.getLogicByRoomId(logicPlayer.getRoomId());
            if( logicTable == null )
            {
                builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_fail.getNumber());
            }
            else
            {
                builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_success.getNumber());
            }
        }

        player.send_msg_to_client(builder);
        return true;
    }
}
