package com.wisp.game.bet.world.proc.c2wLobby;

import client2world_protocols.Client2WorldProtocol;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.world.PlayerSys.GamePlayer;
import com.wisp.game.bet.world.proc.DefaultWorldRequestMessage;
import com.wisp.game.bet.world.unit.WorldPeer;

@IRequest(5048)
public class Packetc2wGetGameList extends DefaultWorldRequestMessage<Client2WorldProtocol.packetc2w_get_gamelist> {
    @Override
    public boolean packet_process(WorldPeer peer, GamePlayer player, Client2WorldProtocol.packetc2w_get_gamelist msg) {

        Client2WorldProtocol.packetw2c_get_gamelist_result.Builder builder = Client2WorldProtocol.packetw2c_get_gamelist_result.newBuilder();

        logger.warn("this protocol must be override");


        return true;
    }
}
