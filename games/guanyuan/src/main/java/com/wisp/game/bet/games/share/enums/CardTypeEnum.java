package com.wisp.game.bet.games.share.enums;

public enum  CardTypeEnum {
    CARD_TYPE_EMPTY(0),
    CARD_TYPE_TIAO(1),
    CARD_TYPE_WANG(2),
    CARD_TYPE_CIRCLE(3),
    CARD_TYPE_ZI(4),
    CARD_TYPE_FEN(5),
    CARD_TYPE_HUA(6)
    ;

    private final int value;
    CardTypeEnum(int val) {
        this.value = val;
    }

    public final int getNumber() { return value; }
}
