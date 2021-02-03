package com.wisp.game.bet.games.share.utils;

import com.wisp.game.bet.games.share.enums.CardTypeEnum;

public class CardType {
    private int start;
    private int end;
    private int repeatNum;      //重复的次数
    private CardTypeEnum type;           //牌的类型 0：杀万饼，1：字牌 2:花牌
    private boolean tingEnable; //是否能够听牌

    public CardType(int start, int end, CardTypeEnum type) {
        this.start = start;
        this.end = end;
        repeatNum = 4;
        this.type = type;
        tingEnable = true;
    }

    public CardType(int start, int end, CardTypeEnum type, boolean tingEnable) {
        this.start = start;
        this.end = end;
        this.repeatNum = 4;
        this.type = type;
        this.tingEnable = tingEnable;
    }

    public CardType(int start, int end, int repeatNum, CardTypeEnum type, boolean tingEnable) {
        this.start = start;
        this.end = end;
        this.repeatNum = repeatNum;
        this.type = type;
        this.tingEnable = tingEnable;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }


    public int getRepeatNum() {
        return repeatNum;
    }

    public void setRepeatNum(int repeatNum) {
        this.repeatNum = repeatNum;
    }

    public CardTypeEnum getType() {
        return type;
    }

    public void setType(CardTypeEnum type) {
        this.type = type;
    }

    public boolean isTingEnable() {
        return tingEnable;
    }

    public void setTingEnable(boolean tingEnable) {
        this.tingEnable = tingEnable;
    }
}
