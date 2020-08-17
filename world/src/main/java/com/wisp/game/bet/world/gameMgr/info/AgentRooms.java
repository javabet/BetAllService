package com.wisp.game.bet.world.gameMgr.info;

import com.wisp.game.bet.db.mongo.games.GameRoomMgrDoc;

import java.util.HashMap;
import java.util.Map;

public class AgentRooms {

    private int gameId;
    private int agentId;
    private boolean init;
    private Map<String, GameRoomMgrDoc> rooms;

    public AgentRooms() {
        rooms = new HashMap<>();
    }

    public void init(int gameId,int agentId)
    {
        init = true;
        this.gameId = gameId;
        this.agentId = agentId;
    }

    public void add_room( GameRoomMgrDoc gameRoomMgrDoc )
    {
        rooms.put(gameRoomMgrDoc.getUid(),gameRoomMgrDoc);
    }

    public GameRoomMgrDoc get_room( String unique_id )
    {
        return rooms.get(unique_id);
    }

    public GameRoomMgrDoc get_room( int roomId )
    {
        for(GameRoomMgrDoc gameRoomMgrDoc : rooms.values())
        {
            if( gameRoomMgrDoc.getRoomId() == roomId )
            {
                return gameRoomMgrDoc;
            }
        }

        return null;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getAgentId() {
        return agentId;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }


    public Map<String, GameRoomMgrDoc> getRooms() {
        return rooms;
    }

    public void setRooms(Map<String, GameRoomMgrDoc> rooms) {
        this.rooms = rooms;
    }
}
