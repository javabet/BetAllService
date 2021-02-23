package com.wisp.game.bet.world.proc.logic2world;

import client2world_protocols.Client2WorldProtocol;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import com.wisp.game.bet.world.PlayerSys.GamePlayer;
import com.wisp.game.bet.world.gameMgr.GamePlayerMgr;
import com.wisp.game.bet.world.unit.WorldPeer;
import logic2world_protocols.Logic2WorldProtocol;
import msg_type_def.MsgTypeDef;

@IRequest
public class Packetl2wPlayerLogoutResult extends DefaultRequestMessage<Logic2WorldProtocol.packetl2w_player_logout_result, WorldPeer> {
    @Override
    public boolean packet_process(WorldPeer peer, Logic2WorldProtocol.packetl2w_player_logout_result msg) {
        GamePlayer gamePlayer = GamePlayerMgr.Instance.findPlayerById(msg.getPlayerid());
        if( gamePlayer == null )
        {
            return true;
        }
        gamePlayer.leave_game();

        if( gamePlayer.getPlayerInfoDoc().isRobot() )
        {
            gamePlayer.player_logout();
        }
        else
        {
            Client2WorldProtocol.packetw2c_leave_game_result.Builder builder = Client2WorldProtocol.packetw2c_leave_game_result.newBuilder();
            builder.setShutdown(msg.getShutdown());
            builder.setResult(MsgTypeDef.e_msg_result_def.e_rmt_success);
            gamePlayer.send_msg_to_client(builder);
        }

        return true;
    }
}
