package com.wisp.game.bet.games.guanyuan.logic;

import com.google.protobuf.ByteString;
import com.wisp.game.bet.games.guanyuan.mgr.GameEngine;
import com.wisp.game.bet.logic.gameMgr.GameManager;
import com.wisp.game.bet.logic.gameObj.GamePlayer;
import game_guanyuan_protocols.GameGuanyunProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class LogicLobby {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ConcurrentHashMap<Integer,LogicTable> tableMap;                     //当前服务器里的桌子数量
    private ConcurrentHashMap<Integer,LogicPlayer> allPlayerMap;                //当前服务器里的玩家
    public LogicLobby() {
        tableMap = new ConcurrentHashMap<>();
        allPlayerMap = new ConcurrentHashMap<>();
    }

    public void heartbeat(double elapsed)
    {
        for(LogicTable logicTable : tableMap.values())
        {
            logicTable.heartheat(elapsed);
        }
    }

    public boolean player_enter_game(GamePlayer gamePlayer, int roomNum, ByteString room_cfg)
    {
        GameGuanyunProtocol.msg_create_room_param create_room_param = null;
        //检查是否有足够的房卡去创建房间
        if( room_cfg != null )
        {
            try
            {
                create_room_param =  GameGuanyunProtocol.msg_create_room_param.parseFrom(room_cfg);
            }
            catch (Exception ex)
            {
                logger.error("parse room cfg has error");
                return false;
            }

            //根据创建局数与创建方式，获得当前需要的房卡数
            int needRoomCard = 1;// create_room_param.getCostType();
            if( gamePlayer.RoomCard < needRoomCard )
            {
                return false;
            }
            roomNum = GameManager.Instance.generate_room_no();
            LogicTable logicTable = new LogicTable(roomNum);
            logicTable.setButton(0);
            logicTable.setNumOfGames(1);
            logicTable.setOwnUid(gamePlayer.get_playerid());
            logicTable.setCreate_room_param(create_room_param);
            tableMap.put(roomNum,logicTable);
        }
        else
        {
            if(!tableMap.containsKey(roomNum))
            {
                return false;
            }

            LogicTable logicTable = tableMap.get(roomNum);
            create_room_param = logicTable.getCreate_room_param();

            int needRoomCard = 1;// create_room_param.getCostType();
            if( gamePlayer.RoomCard < needRoomCard )
            {
                return false;
            }
        }


        //检查是否能够加入到桌子上去
        LogicTable logicTable = tableMap.get(roomNum);
        if(!logicTable.can_add_player(gamePlayer))
        {
            return false;
        }

        LogicPlayer logicPlayer = new LogicPlayer();
        logicPlayer.setGamePlayer(gamePlayer);
        logicPlayer.enter_game();
        allPlayerMap.put(logicPlayer.get_pid(),logicPlayer);
        logicTable.add_player(logicPlayer);

        //将当前玩家加入的情况，广播给桌子上面的其它玩家
        GameGuanyunProtocol.packetl2c_player_enter_game_nt.Builder builder = GameGuanyunProtocol.packetl2c_player_enter_game_nt.newBuilder();
        GameGuanyunProtocol.msg_user_info.Builder msg_user_info_builder = GameGuanyunProtocol.msg_user_info.newBuilder();
        msg_user_info_builder.setSeatPos( logicPlayer.getSeatIndex() );
        msg_user_info_builder.setPlayerId(logicPlayer.get_pid());
        msg_user_info_builder.setUserName( gamePlayer.get_nickname() );
        msg_user_info_builder.setHeadIcon(gamePlayer.get_icon_custom());
        msg_user_info_builder.setSex(0);
        msg_user_info_builder.setScore(0);
        builder.setUserInfo(msg_user_info_builder);
        LogicTable logicTable1 = tableMap.get(roomNum);
        logicTable1.broadcast_msg_to_client(builder);
        return true;
    }

    public boolean remove_player()
    {
        return  true;
    }

    public LogicTable getLogicByRoomId(int roomId)
    {
        if( tableMap.containsKey(roomId) )
        {
            return tableMap.get(roomId);
        }

        return null;
    }

}
