package com.wisp.game.bet.db.mongo.player.doc;

import com.wisp.game.bet.db.mongo.BaseMongoDoc;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Document("OnlineRoomCard")
public class OnlineRoomCardDoc extends BaseMongoDoc {
    @Field(targetType = FieldType.INT32) private int RoomNumber;
    @Field(targetType = FieldType.INT32) private int ServerId;

    public int getRoomNumber() {
        return RoomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        RoomNumber = roomNumber;
    }

    public int getServerId() {
        return ServerId;
    }

    public void setServerId(int serverId) {
        ServerId = serverId;
    }
}
