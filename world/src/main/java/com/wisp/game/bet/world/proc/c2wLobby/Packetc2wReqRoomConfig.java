package com.wisp.game.bet.world.proc.c2wLobby;

import client2world_protocols.Client2WorldProtocol;
import com.wisp.game.bet.db.mongo.games.GameRoomMgrDoc;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.world.PlayerSys.GamePlayer;
import com.wisp.game.bet.world.gameMgr.GameRoomMgr;
import com.wisp.game.bet.world.proc.DefaultWorldRequestMessage;
import com.wisp.game.bet.world.unit.WorldPeer;

import java.util.List;

@IRequest
public class Packetc2wReqRoomConfig extends DefaultWorldRequestMessage<Client2WorldProtocol.packetc2w_req_room_config> {
    @Override
    public boolean packet_process(WorldPeer peer, GamePlayer player, Client2WorldProtocol.packetc2w_req_room_config msg) {

        Client2WorldProtocol.packetw2c_req_room_config_result.Builder builder = Client2WorldProtocol.packetw2c_req_room_config_result.newBuilder();

        List<GameRoomMgrDoc> gameRoomMgrDocList =  GameRoomMgr.Instance.get_room_list(msg.getGameId(),player.getPlayerInfoDoc().getAgentId());

        builder.setGameId(msg.getGameId());
        builder.setEarningsRate(500);
        builder.setAwardOpen(true);
        builder.setEarningsToAward(2000);
        builder.setAwardRate1(50);
        builder.setAwardRate2(100);
        builder.setAwardRate3(150);
        builder.setAwardRate4(900);


        player.send_msg_to_client(builder);
        return true;
    }
}
