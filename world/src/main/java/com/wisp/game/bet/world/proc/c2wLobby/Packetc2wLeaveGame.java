package com.wisp.game.bet.world.proc.c2wLobby;

import client2world_protocols.Client2WorldProtocol;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.RequestMessageFromGate;
import com.wisp.game.bet.world.PlayerSys.GamePlayer;
import com.wisp.game.bet.world.proc.DefaultWorldRequestMessage;
import com.wisp.game.bet.world.unit.WorldPeer;
import logic2world_protocols.Logic2WorldProtocol;

@IRequest(5008)
public class Packetc2wLeaveGame extends DefaultWorldRequestMessage<Client2WorldProtocol.packetc2w_leave_game> {
    @Override
    public boolean packet_process(WorldPeer peer, GamePlayer player, Client2WorldProtocol.packetc2w_leave_game msg) {

        WorldPeer logicPeer = player.get_logic();

        if( logicPeer != null )
        {
            Logic2WorldProtocol.packetw2l_player_logout.Builder builder = Logic2WorldProtocol.packetw2l_player_logout.newBuilder();
            builder.setPlayerid(player.getPlayerInfoDoc().getPlayerId());
            logicPeer.send_msg(builder);
        }

        return true;
    }
}
