package com.wisp.game.bet.games.guanyuan.logic.info;

import com.wisp.game.bet.games.share.enums.HuTypeEnum;

public class HuDetailInfo {
    private HuTypeEnum huId;
    private int score;
    private int linked_pos;

    public HuDetailInfo()
    {
    }

    public HuDetailInfo(HuTypeEnum huId, int score, int linked_pos)
    {
        this.huId = huId;
        this.score = score;
        this.linked_pos = linked_pos;
    }

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

    public int getLinked_pos()
    {
        return linked_pos;
    }

    public void setLinked_pos(int linked_pos)
    {
        this.linked_pos = linked_pos;
    }
}
