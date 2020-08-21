package com.wisp.game.bet.games.baccarat.proc;

import com.wisp.game.bet.games.baccarat.logic.LogicPlayer;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.unit.LogicPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.RequestMessageFromGate;
import game_baccarat_protocols.GameBaccaratProtocol;

@IRequest(10007)
public class Packetc2lGetRoomSceneInfo extends RequestMessageFromGate<GameBaccaratProtocol.packetc2l_get_room_scene_info, LogicPeer, GamePlayer> {
    @Override
    public boolean packet_process(LogicPeer peer, GamePlayer player, GameBaccaratProtocol.packetc2l_get_room_scene_info msg) {

        LogicPlayer logicPlayer = (LogicPlayer)player.getPhandler();
        if( logicPlayer != null && logicPlayer.get_room() != null )
        {
            GameBaccaratProtocol.packetl2c_get_room_scene_info_result.Builder builder =  logicPlayer.get_room().get_room_scene_info(logicPlayer);
            player.send_msg_to_client(builder);

            logicPlayer.get_room().send_cache_msg(logicPlayer);
        }

        return true;
    }
}
