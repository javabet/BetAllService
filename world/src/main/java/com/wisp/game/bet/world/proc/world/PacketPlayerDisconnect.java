package com.wisp.game.bet.world.proc.world;

import com.wisp.game.bet.world.PlayerSys.GamePlayer;
import com.wisp.game.bet.world.gameMgr.GamePlayerMgr;
import com.wisp.game.bet.world.unit.WorldPeer;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import server_protocols.ServerProtocol;

//gate->world
@IRequest
public class PacketPlayerDisconnect extends DefaultRequestMessage<ServerProtocol.packet_player_disconnect, WorldPeer> {
    @Override
    public boolean packet_process(WorldPeer peer, ServerProtocol.packet_player_disconnect msg) {
        GamePlayer gamePlayer = GamePlayerMgr.Instance.find_player( msg.getSessionid() );

        if( gamePlayer != null )
        {
            if(  gamePlayer.get_sessionid() == msg.getSessionid() )
            {
                //这里只做了标记 并未移除数据
                gamePlayer.player_logout();
            }

            GamePlayerMgr.Instance.remove_session(msg.getSessionid());
        }

        return true;
    }
}
