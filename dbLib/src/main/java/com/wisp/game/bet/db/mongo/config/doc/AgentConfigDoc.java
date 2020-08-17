package com.wisp.game.bet.db.mongo.config.doc;

import com.wisp.game.bet.db.mongo.BaseMongoDoc;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Document("AgentConfig")
public class AgentConfigDoc extends BaseMongoDoc {

    private static final long serialVersionUID = 7463117477455793083L;

    @Field(targetType = FieldType.INT32) private int AgentId;

    public int getAgentId() {
        return AgentId;
    }

    public void setAgentId(int agentId) {
        AgentId = agentId;
    }
}
