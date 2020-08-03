package com.wisp.game.bet.world.dbConfig.info;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AgentInfo {
    private String agentId;
    private int agentName;
    private Set<String> channelIds;
    private Map<Integer,AgentGameInfo> gameMap;
    private int updateTime;

    public AgentInfo() {
        gameMap = new HashMap<>();
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public int getAgentName() {
        return agentName;
    }

    public void setAgentName(int agentName) {
        this.agentName = agentName;
    }

    public Set<String> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(Set<String> channelIds) {
        this.channelIds = channelIds;
    }

    public Map<Integer, AgentGameInfo> getGameMap() {
        return gameMap;
    }

    public void setGameMap(Map<Integer, AgentGameInfo> gameMap) {
        this.gameMap = gameMap;
    }


    public int getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }
}
