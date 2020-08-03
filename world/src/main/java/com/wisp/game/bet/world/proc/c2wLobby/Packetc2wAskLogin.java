package com.wisp.game.bet.world.proc.c2wLobby;

import client2world_protocols.Client2WorldProtocol;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.world.PlayerSys.GamePlayer;
import com.wisp.game.bet.world.dbConfig.AgentInfoConfig;
import com.wisp.game.bet.world.dbConfig.info.AgentGameInfo;
import com.wisp.game.bet.world.dbConfig.info.AgentInfo;
import com.wisp.game.bet.world.gameMgr.GameEngineMgr;
import com.wisp.game.bet.world.gameMgr.info.GameInfo;
import com.wisp.game.bet.world.proc.DefaultWorldRequestMessage;
import com.wisp.game.bet.world.unit.WorldPeer;

@IRequest
public class Packetc2wAskLogin extends DefaultWorldRequestMessage<Client2WorldProtocol.packetc2w_ask_login> {
    @Override
    public boolean packet_process(WorldPeer peer, GamePlayer player, Client2WorldProtocol.packetc2w_ask_login msg) {
        Client2WorldProtocol.packetw2c_ask_login_result.Builder builder = Client2WorldProtocol.packetw2c_ask_login_result.newBuilder();
        msg_info_def.MsgInfoDef.msg_account_info.Builder msgAccountInfoBuilder = builder.getAccountInfoBuilder();
        msgAccountInfoBuilder.setAid(player.getM_playerInfo().getPlayerId());
        msgAccountInfoBuilder.setGold(100);

        java.util.List<client2world_protocols.Client2WorldProtocol.msg_game_info.Builder> gameListBuilder = builder.getGameListBuilderList();
         AgentInfo agentInfo =  AgentInfoConfig.Instance.getGameInfo(player.getM_playerInfo().getChannelID());
        if( agentInfo != null )
        {
            for(AgentGameInfo agentGameInfo : agentInfo.getGameMap().values())
            {
                GameInfo gameInfo = GameEngineMgr.Instance.get_game_info(player.getM_playerInfo().getPlayerId(),0);
                client2world_protocols.Client2WorldProtocol.msg_game_info.Builder gameInfoBuilder = client2world_protocols.Client2WorldProtocol.msg_game_info.newBuilder();
                gameInfoBuilder.setGameid( gameInfo.getGameId() );
                gameInfoBuilder.setGamever(gameInfo.getGameVer());
                gameInfoBuilder.setMinVer(0);
                gameInfoBuilder.setIsHot(true);
                gameInfoBuilder.setIsPowerful(true);
                gameInfoBuilder.setSort(agentGameInfo.getSort());
                gameListBuilder.add(gameInfoBuilder);
            }
        }

        if( player.is_gaming() )
        {
            builder.setGaming( player.get_gameid() );
        }

        peer.send_msg(builder.build());

        return true;
    }
}
