package com.wisp.game.bet.games.guanyuan.logic.info;

import java.util.ArrayList;
import java.util.List;

public class HuCircleInfo
{
    private int circleIdx;
    private List<HuCircleItemInfo> huCircleItemInfoList;

    public HuCircleInfo()
    {
        huCircleItemInfoList = new ArrayList<>();
    }

    public int getCircleIdx()
    {
        return circleIdx;
    }

    public void setCircleIdx(int circleIdx)
    {
        this.circleIdx = circleIdx;
    }

    public List<HuCircleItemInfo> getHuCircleItemInfoList()
    {
        return huCircleItemInfoList;
    }

    public void setHuCircleItemInfoList(List<HuCircleItemInfo> huCircleItemInfoList)
    {
        this.huCircleItemInfoList = huCircleItemInfoList;
    }
}
