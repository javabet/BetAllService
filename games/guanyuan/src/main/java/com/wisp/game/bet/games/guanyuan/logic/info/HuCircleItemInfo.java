package com.wisp.game.bet.games.guanyuan.logic.info;

import java.util.ArrayList;
import java.util.List;

//胡一局后的情况
public class HuCircleItemInfo
{
    private int seatPos;
    private int score;
    private List<HuDetailInfo>  huDetailInfos;
    private int huaScore;

    public HuCircleItemInfo()
    {
        huDetailInfos = new ArrayList<>();
    }

    public void addHuDetailInfo( HuDetailInfo huDetailInfo )
    {
        huDetailInfos.add(huDetailInfo);
    }
    public int getSeatPos()
    {
        return seatPos;
    }

    public void setSeatPos(int seatPos)
    {
        this.seatPos = seatPos;
    }

    public int getScore()
    {
        return score;
    }

    public void setScore(int score)
    {
        this.score = score;
    }

    public List<HuDetailInfo> getHuDetailInfos()
    {
        return huDetailInfos;
    }

    public void setHuDetailInfos(List<HuDetailInfo> huDetailInfos)
    {
        this.huDetailInfos = huDetailInfos;
    }

    public int getHuaScore()
    {
        return huaScore;
    }

    public void setHuaScore(int huaScore)
    {
        this.huaScore = huaScore;
    }
}
