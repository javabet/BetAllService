package com.wisp.game.bet.games.share.utils;

import com.wisp.game.bet.games.share.HuStrategy.HistoryActionInfo;
import com.wisp.game.bet.games.share.common.HuPattern;

import java.util.List;
import java.util.Map;

public interface IMaJiangPlayerData
{
    //当前每张牌的值与数量的集合
    public Map<Integer, Integer> getCountMap();

    //玩家手上的牌的集合
    public List<Integer> getHolds();

    //玩家听牌后的集合
    public Map<Integer, HuPattern> getTingMap();

    //玩家出牌后的历史动作列表
    public List<HistoryActionInfo> getPlayerHistoryList();

    //玩家在桌面上抽牌的动作，包括，杠，碰，吃
    public List<HistoryActionInfo> getOutHistoryList();


    //默认花的数量
    public List<Integer> getInitFlowerCards();

    //后期花的列表
    public List<Integer> getFlowers();
}
