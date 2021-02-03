package com.wisp.game.bet.games.share.enums;

public enum HistoryActionEnum {
    HISTORY_ACTION_NULL(-1),
    HISTORY_ACTION_SKIP(0),
    HISTORY_ACTION_CHUPAI(1),
    HISTORY_ACTION_MOPAI(2),
    HISTORY_ACTION_CHI_PAI(3),
    HISTORY_ACTION_PENG(4),
    HISTORY_ACTION_DIAN_GANG(5),
    HISTORY_ACTION_WANG_GANG(6),
    HISTORY_ACTION_AN_GANG(7),
    HISTORY_ACTION_JIAO_TING(8),
    HISTORY_ACTION_HU(9),
    HISTORY_ACTION_HU_QIANG_GANG(10),
    HISTORY_ACTION_HU_ZIMO(11),
    HISTORY_ACTION_HU_DIAN_GANG(12),
    HISTORY_ACTION_HU_GABNG_PAO(13),
    HISTORY_ACTION_HU_FANG_PAO(14),
    ;

    private int actionType;
    public int getValue()
    {
        return actionType;
    }

    private HistoryActionEnum(int value)
    {
        this.actionType = value;
    }
}
