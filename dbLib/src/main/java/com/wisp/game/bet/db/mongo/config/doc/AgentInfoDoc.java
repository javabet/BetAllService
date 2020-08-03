package com.wisp.game.bet.db.mongo.config.doc;

import com.wisp.game.bet.db.mongo.config.doc.info.GameInfo;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collation = "AgentInfo")
public class AgentInfoDoc  {
    private int AgentId;
    private String ChannelId;
    private List<GameInfo> GameInfos;

    public int getAgentId() {
        return AgentId;
    }

    public void setAgentId(int agentId) {
        AgentId = agentId;
    }

    public String getChannelId() {
        return ChannelId;
    }

    public void setChannelId(String channelId) {
        ChannelId = channelId;
    }

    public List<GameInfo> getGameInfos() {
        return GameInfos;
    }

    public void setGameInfos(List<GameInfo> gameInfos) {
        GameInfos = gameInfos;
    }
}
