package com.wisp.game.bet.db.mongo.player.doc;

import com.wisp.game.bet.db.mongo.BaseMongoDoc;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Document("RandomRoomNumber")
public class RandomRoomNumberDoc extends BaseMongoDoc {
    @Field(targetType = FieldType.INT32,name = "Index")private int Index;
    @Field(targetType = FieldType.INT32,name = "RoomNumber")private int RoomNumber;


    public int getIndex() {
        return Index;
    }

    public void setIndex(int index) {
        Index = index;
    }

    public int getRoomNumber() {
        return RoomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        RoomNumber = roomNumber;
    }
}
