package com.wisp.game.bet.world.proc.logic2world;

import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import com.wisp.game.bet.world.PlayerSys.GamePlayer;
import com.wisp.game.bet.world.gameMgr.GamePlayerMgr;
import com.wisp.game.bet.world.unit.WorldPeer;
import logic2world_protocols.Logic2WorldProtocol;

@IRequest
public class Packetw2lChangePlayerProperty extends DefaultRequestMessage<Logic2WorldProtocol.packetw2l_change_player_property, WorldPeer> {
    @Override
    public boolean packet_process(WorldPeer peer, Logic2WorldProtocol.packetw2l_change_player_property msg) {
        GamePlayer gamePlayer = GamePlayerMgr.Instance.findPlayerById(msg.getPlayerid());
        if( gamePlayer == null || gamePlayer.getPlayerInfoDoc().isRobot()  )
        {
            return  true;
        }

        if( msg.hasChangeInfo() )
        {
            if( msg.getChangeInfo().hasGold() )
            {
                gamePlayer.change_gold((int)msg.getChangeInfo().getGold(),true);
            }
        }

        if( msg.hasChangeInfoEx() )
        {
            if( msg.getChangeInfoEx().hasFreeGold() )
            {
                //
            }
        }


        return true;
    }
}
