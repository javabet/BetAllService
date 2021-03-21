package com.wisp.game.bet.games.share.enums;

public enum HuTypeEnum {

    TYPE_NORMAL(0),     //普通小胡
    TYPE_SEVEN(1),      //七对
    TYPE_DUI_DUI_HU(2), //对对胡
    TYPE_JIE_ZHANG(3),  //绝张
    TYPE_QI_YI_SE(4),   //青一色
    TYPE_INIT_HUA(5),   //花分
    TYPE_AN_KE(6),      //暗刻
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
