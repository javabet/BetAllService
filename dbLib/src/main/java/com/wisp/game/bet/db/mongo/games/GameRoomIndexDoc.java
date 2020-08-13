package com.wisp.game.bet.db.mongo.games;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Document(collection = "GameRoomIndex" )
public class GameRoomIndexDoc {
    @Field(targetType = FieldType.INT32) private int GameId;
    @Field(targetType = FieldType.INT32) private int Index;

    public int getGameId() {
        return GameId;
    }

    public void setGameId(int gameId) {
        GameId = gameId;
    }

    public int getIndex() {
        return Index;
    }

    public void setIndex(int index) {
        Index = index;
    }
}
