package com.wisp.game.bet.games.guanyuan.doc;

import com.wisp.game.bet.db.mongo.BaseMongoDoc;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Document("GuanyunPlayer")
public class GuanYunPlayerDoc extends BaseMongoDoc {
    @Field(targetType = FieldType.INT32)public int PlayerId;

    public int getPlayerId() {
        return PlayerId;
    }

    public void setPlayerId(int playerId) {
        PlayerId = playerId;
    }
}
