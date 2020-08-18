package com.wisp.game.bet.world.proc.c2wLobby;

import client2world_protocols.Client2WorldProtocol;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.world.PlayerSys.GamePlayer;
import com.wisp.game.bet.world.gameMgr.GameEngineMgr;
import com.wisp.game.bet.world.proc.DefaultWorldRequestMessage;
import com.wisp.game.bet.world.unit.WorldPeer;
import msg_type_def.MsgTypeDef;

@IRequest
public class Packetc2wCheckGameState extends DefaultWorldRequestMessage<Client2WorldProtocol.packetc2w_check_game_state> {
    @Override
    public boolean packet_process(WorldPeer peer, GamePlayer player, Client2WorldProtocol.packetc2w_check_game_state msg) {
        GameEngineMgr.GameInfoStruct gameInfoStruct = GameEngineMgr.Instance.get_game_info_struct(msg.getGameId());

        Client2WorldProtocol.packetw2c_check_game_state_result.Builder builder = Client2WorldProtocol.packetw2c_check_game_state_result.newBuilder();
        builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_success);

        builder.setYyhdEnable(true);
        builder.setWxdlEnable(true);

        if( gameInfoStruct.serverId <= 0 )
        {
            builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_server_down);
        }

        player.send_msg_to_client(builder);
        return true;
    }
}
