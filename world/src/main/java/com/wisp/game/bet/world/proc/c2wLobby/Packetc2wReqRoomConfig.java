package com.wisp.game.bet.world.proc.c2wLobby;

import client2world_protocols.Client2WorldProtocol;
import com.wisp.game.bet.db.mongo.games.GameRoomMgrDoc;
import com.wisp.game.bet.share.netty.IRequest;
import com.wisp.game.bet.world.PlayerSys.GamePlayer;
import com.wisp.game.bet.world.dbConfig.AgentConfig;
import com.wisp.game.bet.world.gameMgr.GameRoomMgr;
import com.wisp.game.bet.world.proc.DefaultWorldRequestMessage;
import com.wisp.game.bet.world.unit.WorldPeer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@IRequest(5175)
public class Packetc2wReqRoomConfig extends DefaultWorldRequestMessage<Client2WorldProtocol.packetc2w_req_room_config> {

    @Autowired
    private AgentConfig agentConfig;

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

        List<Integer> custom_list =  agentConfig.game_custom_list(player.getPlayerInfoDoc().getAgentId(),msg.getGameId());

        for( GameRoomMgrDoc gameRoomMgrDoc : gameRoomMgrDocList )
        {
            client2world_protocols.Client2WorldProtocol.msg_room_config.Builder roomConfigBuilder = client2world_protocols.Client2WorldProtocol.msg_room_config.newBuilder();

            roomConfigBuilder.setRoomId(gameRoomMgrDoc.getRoomId());
            roomConfigBuilder.setUniqueId(gameRoomMgrDoc.getUid());
            roomConfigBuilder.setBankerCondition(gameRoomMgrDoc.getRoomId());
            roomConfigBuilder.setFirstBankerCost(gameRoomMgrDoc.getFirstBankerCost());
            roomConfigBuilder.setAddBankerCost(gameRoomMgrDoc.getAddBankerCost());
            roomConfigBuilder.setAutoLeaveBanker(gameRoomMgrDoc.getAutoLeaveBanker());
            roomConfigBuilder.setPlayerMaxCount(gameRoomMgrDoc.getPlayerMaxCount());
            roomConfigBuilder.setGoldCondition(gameRoomMgrDoc.getGoldCondition());
            roomConfigBuilder.setBetCondition(gameRoomMgrDoc.getBetCondition());
            roomConfigBuilder.setBaseGold(gameRoomMgrDoc.getBaseGold());
            roomConfigBuilder.setRoomName(gameRoomMgrDoc.getRoomName());
            roomConfigBuilder.setRoomidTxt(gameRoomMgrDoc.getRoomIDTxt());
            roomConfigBuilder.setRoomType(gameRoomMgrDoc.getRoomType());
            roomConfigBuilder.setRoomNameType(gameRoomMgrDoc.getRoomNameType());
            roomConfigBuilder.addCustomList(gameRoomMgrDoc.getCarryRestriction());
            roomConfigBuilder.setBigBlind(gameRoomMgrDoc.getBigBlind());
            roomConfigBuilder.setSmallBlind(gameRoomMgrDoc.getSmallBlind());
            roomConfigBuilder.setFreeGold((int)gameRoomMgrDoc.getFreeGold());
            roomConfigBuilder.setMaxAnte(gameRoomMgrDoc.getMaxAnte());
            roomConfigBuilder.setHuaGold(gameRoomMgrDoc.getHuaGold());

            roomConfigBuilder.addAllWeightList(convertListToLong(gameRoomMgrDoc.getWeightList()));
            roomConfigBuilder.addAllBetLimit(convertListToLong(gameRoomMgrDoc.getBetLimit()));
            roomConfigBuilder.addAllBetRange(convertListToLong(gameRoomMgrDoc.getBetRange()));

            if( custom_list != null && custom_list.size() > 0 )
            {
                roomConfigBuilder.addAllCustomList( convertListToLong(custom_list) );
            }
            else
            {
                roomConfigBuilder.addAllCustomList(convertListToLong(gameRoomMgrDoc.getCustomList()));
            }
            roomConfigBuilder.addAllPlatList( convertListToLong(gameRoomMgrDoc.getPlatList()) );
            roomConfigBuilder.addAllRateList(gameRoomMgrDoc.getmRatePoolList());

            builder.addRoomCfg(roomConfigBuilder);
        }

        player.send_msg_to_client(builder);
        return true;
    }


    private List<Long> convertListToLong(List<Integer> list)
    {
        List<Long> longList = new ArrayList<>();
        for(int i = 0; i < list.size();i++)
        {
            longList.add( list.get(i).longValue() );
        }

        return longList;
    }

}
