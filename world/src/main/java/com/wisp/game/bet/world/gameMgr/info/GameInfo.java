package com.wisp.game.bet.world.gameMgr.info;

import msg_info_def.MsgInfoDef;
import msg_type_def.MsgTypeDef;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameInfo {
    private int GameId;
    private int GameVer;
    private int minVer;
    private List<String> H5GameVer;
    private Map<Integer,Integer> ServersMap;
    private MsgInfoDef.msg_roomcard_config roomcard_config;

    public GameInfo() {
        ServersMap = new HashMap<>();
    }

    public int getGameId() {
        return GameId;
    }

    public void setGameId(int gameId) {
        GameId = gameId;
    }

    public int getGameVer() {
        return GameVer;
    }

    public void setGameVer(int gameVer) {
        GameVer = gameVer;
    }

    public int getMinVer() {
        return minVer;
    }

    public void setMinVer(int minVer) {
        this.minVer = minVer;
    }

    public Map<Integer, Integer> getServersMap() {
        return ServersMap;
    }

    public void setServersMap(Map<Integer, Integer> serversMap) {
        ServersMap = serversMap;
    }

    public MsgInfoDef.msg_roomcard_config getRoomcard_config() {
        return roomcard_config;
    }

    public void setRoomcard_config(MsgInfoDef.msg_roomcard_config roomcard_config) {
        this.roomcard_config = roomcard_config;
    }

    public List<String> getH5GameVer() {
        return H5GameVer;
    }

    public void setH5GameVer(List<String> h5GameVer) {
        H5GameVer = h5GameVer;
    }
}
