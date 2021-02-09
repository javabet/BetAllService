package com.wisp.game.bet.db.mongo.player.doc;

import com.wisp.game.bet.db.mongo.BaseMongoDoc;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Document("RandomRoomNumber")
public class RandomRoomNumberDoc extends BaseMongoDoc {
    @Field(targetType = FieldType.INT32,name = "Index")private int Index;
    @Field(targetType = FieldType.INT32,name = "Value")private int Value;


    public int getIndex() {
        return Index;
    }

    public void setIndex(int index) {
        Index = index;
    }

    public int getValue()
    {
        return Value;
    }

    public void setValue(int value)
    {
        Value = value;
    }
}
