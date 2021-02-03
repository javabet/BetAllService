package com.wisp.game.bet.games.share.enums;

public enum ActionTypeEnum {
    ACTION_AN_GANG(0x01),
    ACTION_DIAN_GANG(0x02),
    ACTION_WANG_GANG(0x04),
    ACTION_PENG(0x08),
    ACTION_HU(0x10),
    ACTION_CHI_PAI(0x20),
    ACTION_CHU_PAI(0x40),
    ACTION_JIAO_TING(0x80),
    ;

    public int getActionType()
    {
        return actionType;
    }
    private int actionType;
    private ActionTypeEnum(int value) {
        this.actionType = value;
    }
}
