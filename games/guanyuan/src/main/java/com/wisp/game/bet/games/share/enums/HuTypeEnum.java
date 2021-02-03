package com.wisp.game.bet.games.share.enums;

public enum HuTypeEnum {

    TYPE_SEVEN(0),
    TYPE_DUI_DUI_HU(1),
    TYPE_NORMAL(2)
    ;

    private int type;
    private int getValue()
    {
        return type;
    }

    private HuTypeEnum(int val)
    {
        this.type = val;
    }

}
