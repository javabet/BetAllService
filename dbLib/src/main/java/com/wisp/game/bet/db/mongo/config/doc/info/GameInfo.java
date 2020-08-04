package com.wisp.game.bet.db.mongo.config.doc.info;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

public class GameInfo {
    @Field( targetType = FieldType.INT32)
    private int GameId;
    @Field( targetType = FieldType.INT32)
    private int Sort;
    @Field( targetType = FieldType.INT32)
    private int IsHot;

    public int getGameId() {
        return GameId;
    }

    public void setGameId(int gameId) {
        GameId = gameId;
    }

    public int getSort() {
        return Sort;
    }

    public void setSort(int sort) {
        Sort = sort;
    }

    public int getIsHot() {
        return IsHot;
    }

    public void setIsHot(int isHot) {
        IsHot = isHot;
    }
}
