package com.wisp.game.bet.games.baccarat.proc;

import com.wisp.game.bet.games.baccarat.logic.LogicLobby;
import com.wisp.game.bet.games.baccarat.logic.LogicPlayer;
import com.wisp.game.bet.games.baccarat.mgr.GameEngine;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.unit.LogicPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.RequestMessageFromGate;
import game_baccarat_protocols.GameBaccaratProtocol;
import msg_type_def.MsgTypeDef;
import org.springframework.beans.factory.annotation.Autowired;


//进入房间
@IRequest
public class Packetc2lEnterRoom extends RequestMessageFromGate<GameBaccaratProtocol.packetc2l_enter_room, LogicPeer, GamePlayer> {

    @Autowired
    private LogicLobby logicLobby;

    @Override
    public boolean packet_process(LogicPeer peer, GamePlayer player, GameBaccaratProtocol.packetc2l_enter_room msg) {

        GameBaccaratProtocol.packetl2c_enter_room_result.Builder builder = GameBaccaratProtocol.packetl2c_enter_room_result.newBuilder();

        int ret =  logicLobby.enter_room(player.get_playerid(),msg.getRoomid());
        builder.setResult(MsgTypeDef.e_msg_result_def.valueOf(ret));
        player.send_msg_to_client(builder);

        if( MsgTypeDef.e_msg_result_def.e_rmt_success.getNumber() == ret )
        {
            LogicPlayer logicPlayer = (LogicPlayer)player.getPhandler();
            GameBaccaratProtocol.packetl2c_get_room_scene_info_result.Builder room_scene_info_builder =  logicPlayer.get_room().get_room_scene_info(null);
            player.send_msg_to_client(room_scene_info_builder);
        }

        return true;
    }
}
