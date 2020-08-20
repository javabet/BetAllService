package com.wisp.game.bet.world.proc.c2wLobby;

import client2world_protocols.Client2WorldProtocol;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.RequestMessageFromGate;
import com.wisp.game.bet.world.PlayerSys.GamePlayer;
import com.wisp.game.bet.world.gameMgr.GameEngineMgr;
import com.wisp.game.bet.world.proc.DefaultWorldRequestMessage;
import com.wisp.game.bet.world.unit.WorldPeer;
import msg_type_def.MsgTypeDef;

@IRequest(5004)
public class Packetc2wEnterGame extends DefaultWorldRequestMessage<Client2WorldProtocol.packetc2w_enter_game> {
    @Override
    public boolean packet_process(WorldPeer peer, GamePlayer player, Client2WorldProtocol.packetc2w_enter_game msg) {
        Client2WorldProtocol.packetw2c_enter_game_result.Builder builder =  Client2WorldProtocol.packetw2c_enter_game_result.newBuilder();

         GameEngineMgr.GameInfoStruct gameInfoStruct = GameEngineMgr.Instance.get_game_info_struct(msg.getGameid());

         if( gameInfoStruct != null  )
         {
            if( player.join_game(msg.getGameid(),gameInfoStruct.serverId,msg.getRoomid()) )
            {
                return true;
            }
         }
         else
         {
            builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_shutdown);
         }

         peer.send_msg_to_client(player.get_sessionid(),builder);

        return true;
    }
}
