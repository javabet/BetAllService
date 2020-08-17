package com.wisp.game.bet.db.mongo.config.doc;

import com.wisp.game.bet.db.mongo.config.doc.info.GameInfoChildDoc;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.List;

@Document(collection = "AgentInfo")
public class AgentInfoDoc  {
    @Field( targetType = FieldType.INT32)
    private int AgentId;
    private String ChannelId;
    private List<GameInfoChildDoc> GameInfos;

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

    public List<GameInfoChildDoc> getGameInfos() {
        return GameInfos;
    }

    public void setGameInfos(List<GameInfoChildDoc> gameInfos) {
        GameInfos = gameInfos;
    }
}
