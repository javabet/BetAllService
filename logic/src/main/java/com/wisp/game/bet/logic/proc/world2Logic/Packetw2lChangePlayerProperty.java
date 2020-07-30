package com.wisp.game.bet.logic.proc.world2Logic;

import com.wisp.game.bet.logic.gameMgr.GamePlayerMgr;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import com.wisp.game.bet.logic.unit.ServerPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import logic2world_protocols.Logic2WorldProtocol;

@IRequest
public class Packetw2lChangePlayerProperty extends DefaultRequestMessage<Logic2WorldProtocol.packetw2l_change_player_property, ServerPeer> {
    @Override
    public boolean packet_process(ServerPeer peer, Logic2WorldProtocol.packetw2l_change_player_property msg) {
        GamePlayer gamePlayer = GamePlayerMgr.Instance.find_playerbyid(msg.getPlayerid());
        if( gamePlayer != null )
        {
            if( msg.hasChangeInfo() )
            {
                msg_info_def.MsgInfoDef.msg_account_info msg_account_info = msg.getChangeInfo();
                if( msg_account_info.hasGold() )
                {

                }
            }
        }

        return true;
    }
}
