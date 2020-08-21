package com.wisp.game.bet.games.baccarat.proc;

import com.wisp.game.bet.games.baccarat.logic.LogicLobby;
import com.wisp.game.bet.games.baccarat.logic.LogicRoom;
import com.wisp.game.bet.games.baccarat.mgr.GameEngine;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.unit.LogicPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import com.wisp.game.bet.share.netty.PacketManager.RequestMessageFromGate;
import game_baccarat_protocols.GameBaccaratProtocol;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@IRequest(10001)
public class Packetc2lGetRoomInfo extends RequestMessageFromGate<GameBaccaratProtocol.packetc2l_get_room_info, LogicPeer,GamePlayer> {

    @Autowired
    private LogicLobby logicLobby;

    @Override
    public boolean packet_process(LogicPeer peer, GamePlayer player, GameBaccaratProtocol.packetc2l_get_room_info msg) {
        GameBaccaratProtocol.packetl2c_get_room_info_result.Builder builder = GameBaccaratProtocol.packetl2c_get_room_info_result.newBuilder();

        Map<Integer, LogicRoom> room_list =  logicLobby.get_rooms();
        for( LogicRoom logicRoom : room_list.values() )
        {
            game_baccarat_protocols.GameBaccaratProtocol.msg_room_info.Builder room_info_builder = game_baccarat_protocols.GameBaccaratProtocol.msg_room_info.newBuilder();
            room_info_builder.setRoomid(logicRoom.get_room_id());
            builder.addRoomList(room_info_builder);
        }


        player.send_msg_to_client(builder);

        return true;
    }
}
