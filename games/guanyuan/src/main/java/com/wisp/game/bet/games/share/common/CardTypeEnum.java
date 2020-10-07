package com.wisp.game.bet.games.share.common;

public enum  CardTypeEnum {
    CARD_TYPE_NORMAL(1),
    CARD_TYPE_FEN(2),
    CARD_TYPE_HUA(3);

    private final int value;
    CardTypeEnum(int val) {
        this.value = val;
    }

    public final int getNumber() { return value; }
}
