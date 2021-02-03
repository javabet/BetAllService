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

@IRequest(10006)
public class Packetc2lGps  extends RequestMessageFromGate<GameGuanyunProtocol.packetc2l_gps, LogicPeer, GamePlayer> {

    @Autowired
    private LogicLobby logicLobby;

    @Override
    public boolean packet_process(LogicPeer peer, GamePlayer player, GameGuanyunProtocol.packetc2l_gps msg) {

        LogicPlayer logicPlayer = (LogicPlayer)player.getPhandler();
        LogicTable logicTable = logicLobby.getLogicByRoomId(logicPlayer.getRoomId());

        GameGuanyunProtocol.packetl2c_gps_result.Builder builder = GameGuanyunProtocol.packetl2c_gps_result.newBuilder();

        if( logicTable == null )
        {
            builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_fail);      //房间不存在
            player.send_msg_to_client(builder);
            return true;
        }

        logicPlayer.setLng(msg.getGpsInfo().getLng());
        logicPlayer.setLat(msg.getGpsInfo().getLat());
        logicPlayer.setGpsStatus(msg.getGpsInfo().getStatus());
        logicPlayer.setAddress(msg.getGpsInfo().getAddress());

        //将玩家的gps信息广播给当前桌子上面的人

        for(LogicPlayer logicPlayer1 : logicTable.getPlayerMap().values())
        {
            GameGuanyunProtocol.msg_gps_info.Builder gpsInfoBuilder = GameGuanyunProtocol.msg_gps_info.newBuilder();
            gpsInfoBuilder.setSeatPos(logicPlayer1.getSeatIndex());
            gpsInfoBuilder.setLng(logicPlayer1.getLng());
            gpsInfoBuilder.setLat(logicPlayer1.getLat());
            gpsInfoBuilder.setStatus(logicPlayer1.getGpsStatus());
            gpsInfoBuilder.setAddress(logicPlayer1.getAddress());
            builder.addGpsInfos(gpsInfoBuilder);
        }

        logicTable.broadcast_msg_to_client(builder);

        return true;
    }
}
