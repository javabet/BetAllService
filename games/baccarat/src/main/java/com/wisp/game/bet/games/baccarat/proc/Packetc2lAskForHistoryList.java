package com.wisp.game.bet.games.baccarat.proc;

import com.wisp.game.bet.games.baccarat.logic.LogicPlayer;
import com.wisp.game.bet.games.baccarat.logic.LogicRoom;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.unit.LogicPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.RequestMessageFromGate;
import game_baccarat_protocols.GameBaccaratProtocol;

//请求牌路
@IRequest
public class Packetc2lAskForHistoryList extends RequestMessageFromGate<GameBaccaratProtocol.packetc2l_ask_for_history_list, LogicPeer, GamePlayer> {
    @Override
    public boolean packet_process(LogicPeer peer, GamePlayer player, GameBaccaratProtocol.packetc2l_ask_for_history_list msg) {

        GameBaccaratProtocol.packetl2c_ask_for_history_list_result.Builder builder = GameBaccaratProtocol.packetl2c_ask_for_history_list_result.newBuilder();
        LogicPlayer logicPlayer = (LogicPlayer)player.getPhandler();
        LogicRoom logicRoom = logicPlayer.get_room();
        if( logicRoom != null )
        {
            builder = logicRoom.get_room_history_list();
        }

        player.send_msg_to_client(builder);

        return true;
    }
}
