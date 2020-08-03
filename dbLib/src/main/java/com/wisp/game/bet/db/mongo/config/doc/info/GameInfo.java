package com.wisp.game.bet.db.mongo.config.doc.info;

public class GameInfo {
    private int GameId;
    private int Sort;
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
