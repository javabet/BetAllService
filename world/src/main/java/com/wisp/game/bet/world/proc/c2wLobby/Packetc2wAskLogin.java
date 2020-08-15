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

import java.util.ArrayList;

@IRequest
public class Packetc2wAskLogin extends DefaultWorldRequestMessage<Client2WorldProtocol.packetc2w_ask_login> {
    @Override
    public boolean packet_process(WorldPeer peer, GamePlayer player, Client2WorldProtocol.packetc2w_ask_login msg) {
        Client2WorldProtocol.packetw2c_ask_login_result.Builder builder = Client2WorldProtocol.packetw2c_ask_login_result.newBuilder();
        msg_info_def.MsgInfoDef.msg_account_info.Builder msgAccountInfoBuilder = builder.getAccountInfoBuilder();
        msgAccountInfoBuilder.setAid(player.getPlayerInfoDoc().getPlayerId());
        msgAccountInfoBuilder.setNickname( player.getPlayerInfoDoc().getNickName() );
        msgAccountInfoBuilder.setGold( player.getPlayerInfoDoc().getGold() );
        msgAccountInfoBuilder.setViplvl(player.getPlayerInfoDoc().getVipLevel());
        msgAccountInfoBuilder.setVipexp(player.getPlayerInfoDoc().getVipExp());
        msgAccountInfoBuilder.setRecharged( player.getPlayerInfoDoc().getRecharged() );
        msgAccountInfoBuilder.setCreateTime( ((Long)(player.getPlayerInfoDoc().getCreateTime().getTime()/1000)).intValue() );
        msgAccountInfoBuilder.setRoomCard(player.getPlayerInfoDoc().getRoomCard());
        msgAccountInfoBuilder.setGuildId(0);
        msgAccountInfoBuilder.setReqJoinGuildTime(0);


        msgAccountInfoBuilder.setIconCustom("head_nan_1.png");
        msgAccountInfoBuilder.setIpinfo(player.getPlayerInfoDoc().getLastIp());
        msgAccountInfoBuilder.setChannelId(player.getPlayerInfoDoc().getChannelID());

        msgAccountInfoBuilder.setSex( player.getPlayerInfoDoc().getSex() );
        msgAccountInfoBuilder.setTicket(0);
        msgAccountInfoBuilder.setSafeBoxGold(0);


        java.util.List<client2world_protocols.Client2WorldProtocol.msg_game_info.Builder> gameListBuilder = new ArrayList<>();
         AgentInfo agentInfo =  AgentInfoConfig.Instance.getGameInfo(player.getPlayerInfoDoc().getChannelID());
        if( agentInfo != null )
        {
            for(AgentGameInfo agentGameInfo : agentInfo.getGameMap().values())
            {
                GameInfo gameInfo = GameEngineMgr.Instance.get_game_info(agentGameInfo.getGameId());
                client2world_protocols.Client2WorldProtocol.msg_game_info.Builder gameInfoBuilder = client2world_protocols.Client2WorldProtocol.msg_game_info.newBuilder();
                gameInfoBuilder.setGameid( gameInfo.getGameId() );
                gameInfoBuilder.setGamever(gameInfo.getGameVer());
                gameInfoBuilder.setMinVer( gameInfo.getMinVer() );
                gameInfoBuilder.setIsHot(true);
                gameInfoBuilder.setIsPowerful(true);
                gameInfoBuilder.setCurOnlineNum(10);
                gameInfoBuilder.setSort(agentGameInfo.getSort());
                builder.addGameList(gameInfoBuilder);
            }
        }



        if( player.is_gaming() )
        {
            builder.setGaming( player.get_gameid() );
        }

        msgAccountInfoBuilder.setUpdateNicknameCount(0);
        msgAccountInfoBuilder.setIsBindMobilePhone(false);
        msgAccountInfoBuilder.setPrivilege(0);
        msgAccountInfoBuilder.setLastGameId( player.getPlayerInfoDoc().getLastGameId() );

        builder.setBinGold(100);
        builder.setBindPhoneDemo(false);


        peer.send_msg_to_client(player.get_sessionid(),builder);

        logger.info("Packetc2wAskLogin has done,ready to send_to_client:" +  builder.getPacketId() );

        return true;
    }
}
