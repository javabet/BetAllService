package com.wisp.game.bet.games.baccarat.proc;

import com.wisp.game.bet.games.baccarat.logic.LogicPlayer;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.unit.LogicPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.RequestMessageFromGate;
import game_baccarat_protocols.GameBaccaratProtocol;

//请求玩家列表
@IRequest(10018)
public class Packetc2lAskPlayerList extends RequestMessageFromGate<GameBaccaratProtocol.packetc2l_ask_playerlist, LogicPeer, GamePlayer> {
    @Override
    public boolean packet_process(LogicPeer peer, GamePlayer player, GameBaccaratProtocol.packetc2l_ask_playerlist msg) {

        LogicPlayer logicPlayer = (LogicPlayer) player.getPhandler();

        if( logicPlayer != null && logicPlayer.get_room() != null )
        {
            GameBaccaratProtocol.packetl2c_playerlist_result.Builder builder =  logicPlayer.get_room().get_player_list();
            if( builder != null )
            {
                player.send_msg_to_client(builder);
            }
        }


        return true;
    }
}
