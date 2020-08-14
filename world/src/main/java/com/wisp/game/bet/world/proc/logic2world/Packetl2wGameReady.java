package com.wisp.game.bet.world.proc.logic2world;

import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.share.netty.PacketManager.DefaultRequestMessage;
import com.wisp.game.bet.world.gameMgr.GameEngineMgr;
import com.wisp.game.bet.world.unit.WorldPeer;
import logic2world_protocols.Logic2WorldProtocol;

//游戏准备好了
@IRequest
public class Packetl2wGameReady extends DefaultRequestMessage<Logic2WorldProtocol.packetl2w_game_ready,WorldPeer> {
    @Override
    public boolean packet_process(WorldPeer peer, Logic2WorldProtocol.packetl2w_game_ready msg) {

        peer.setGameid(msg.getGameId());

        boolean flag = GameEngineMgr.Instance.add_game_info(msg.getGameId(),msg.getGameVer(),peer.get_remote_id());

        if( !flag )
        {
            logger.info("regedit game error  gameid:" + msg.getGameId() + " serverId:" + peer.get_remote_id());
        }


        return true;
    }
}
