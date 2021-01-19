package com.wisp.game.bet.games.guanyuan.logic;

import com.google.protobuf.ByteString;
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

    public boolean player_enter_game(GamePlayer gamePlayer, int roomNum,int room_cfg_type, ByteString room_cfg)
    {
        //查找此房间是否存在
        if( !tableMap.containsKey(roomNum) )
        {
            LogicTable logicTable = new LogicTable(roomNum);
            tableMap.put(roomNum,logicTable);
            logicTable.setButton(0);
            logicTable.setNumOfGames(0);
            logicTable.setOwnUid(gamePlayer.get_playerid());
            logicTable.set_room_cfg(room_cfg);
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

        return false;
    }

}
