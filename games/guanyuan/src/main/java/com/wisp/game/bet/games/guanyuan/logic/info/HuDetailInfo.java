package com.wisp.game.bet.games.guanyuan.logic.info;

import com.wisp.game.bet.games.share.enums.HuTypeEnum;

public class HuDetailInfo {
    private HuTypeEnum huId;
    private int score;

    public HuTypeEnum getHuId() {
        return huId;
    }

    public void setHuId(HuTypeEnum huId) {
        this.huId = huId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
