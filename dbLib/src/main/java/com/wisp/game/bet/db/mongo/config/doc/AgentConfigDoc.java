package com.wisp.game.bet.db.mongo.config.doc;

import com.wisp.game.bet.db.mongo.BaseMongoDoc;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Document("AgentConfig")
public class AgentConfigDoc extends BaseMongoDoc {

    private static final long serialVersionUID = 7463117477455793083L;

    @Field(targetType = FieldType.INT32) private int AgentId;
    private boolean ShareServer;
    @Field(targetType = FieldType.INT32)private int NewGold;


    public int getAgentId() {
        return AgentId;
    }

    public void setAgentId(int agentId) {
        AgentId = agentId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public boolean isShareServer() {
        return ShareServer;
    }

    public void setShareServer(boolean shareServer) {
        ShareServer = shareServer;
    }

    public int getNewGold() {
        return NewGold;
    }

    public void setNewGold(int newGold) {
        NewGold = newGold;
    }
}
