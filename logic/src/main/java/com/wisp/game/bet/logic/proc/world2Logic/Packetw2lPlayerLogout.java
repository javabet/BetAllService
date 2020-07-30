package com.wisp.game.bet.logic.proc.world2Logic;

import com.wisp.game.bet.logic.gameMgr.GamePlayerMgr;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.unit.LogicPeer;
import com.wisp.game.bet.logic.unit.ServerPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import logic2world_protocols.Logic2WorldProtocol;

@IRequest
public class Packetw2lPlayerLogout  extends DefaultRequestMessage<Logic2WorldProtocol.packetw2l_player_logout, ServerPeer> {
    @Override
    public boolean packet_process(ServerPeer peer, Logic2WorldProtocol.packetw2l_player_logout msg) {
        GamePlayer gamePlayer = GamePlayerMgr.Instance.find_playerbyid(msg.getPlayerid());
        if( gamePlayer != null )
        {
            if( gamePlayer.leave_game(false) )
            {
                GamePlayerMgr.Instance.remove_player(gamePlayer);
            }
        }

        return true;
    }
}
