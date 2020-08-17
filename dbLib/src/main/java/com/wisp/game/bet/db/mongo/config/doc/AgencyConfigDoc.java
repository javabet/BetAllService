package com.wisp.game.bet.db.mongo.config.doc;

import com.wisp.game.bet.db.mongo.BaseMongoDoc;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Document
public class AgencyConfigDoc extends BaseMongoDoc {

    private static final long serialVersionUID = 3090737608797770174L;
    @Field(targetType = FieldType.INT32) private int AgentId;

    private String Content;

    private String Name;

    @Field(targetType = FieldType.INT32)private int Type;

    public int getAgentId() {
        return AgentId;
    }

    public void setAgentId(int agentId) {
        AgentId = agentId;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

}
