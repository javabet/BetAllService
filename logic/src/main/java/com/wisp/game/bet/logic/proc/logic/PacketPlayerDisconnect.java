package com.wisp.game.bet.logic.proc.logic;

import com.wisp.game.bet.logic.gameMgr.GamePlayerMgr;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.sshare.e_player_state;
import com.wisp.game.bet.logic.unit.LogicPeer;
import com.wisp.game.bet.logic.unit.ServerPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerProtocol;

@IRequest
public class PacketPlayerDisconnect extends DefaultRequestMessage<ServerProtocol.packet_player_disconnect, ServerPeer> {
    @Override
    public boolean packet_process(ServerPeer peer, ServerProtocol.packet_player_disconnect msg) {

        GamePlayer gamePlayer = GamePlayerMgr.Instance.find_player(msg.getSessionid());
        if( gamePlayer != null )
        {
            gamePlayer.set_state(e_player_state.e_ps_disconnect);
        }

        return true;
    }
}
