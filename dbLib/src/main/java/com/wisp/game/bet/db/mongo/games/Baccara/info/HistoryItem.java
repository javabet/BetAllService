package com.wisp.game.bet.db.mongo.games.Baccara.info;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

public class HistoryItem {
    private boolean IsTie;
    private boolean IsPlayerWin;
    private boolean IsPlayerPair;
    private boolean IsBankerPair;
    private boolean IsBankerWin;
    @Field(targetType = FieldType.INT32)private int WinPoint;

    public boolean isTie() {
        return IsTie;
    }

    public void setTie(boolean tie) {
        IsTie = tie;
    }

    public boolean isPlayerWin() {
        return IsPlayerWin;
    }

    public void setPlayerWin(boolean playerWin) {
        IsPlayerWin = playerWin;
    }

    public boolean isPlayerPair() {
        return IsPlayerPair;
    }

    public void setPlayerPair(boolean playerPair) {
        IsPlayerPair = playerPair;
    }

    public boolean isBankerPair() {
        return IsBankerPair;
    }

    public void setBankerPair(boolean bankerPair) {
        IsBankerPair = bankerPair;
    }

    public boolean isBankerWin() {
        return IsBankerWin;
    }

    public void setBankerWin(boolean bankerWin) {
        IsBankerWin = bankerWin;
    }

    public int getWinPoint() {
        return WinPoint;
    }

    public void setWinPoint(int winPoint) {
        WinPoint = winPoint;
    }
}
