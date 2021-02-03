package com.wisp.game.bet.games.guanyuan.logic.info;

import com.wisp.game.bet.games.share.HuStrategy.HistoryActionInfo;
import com.wisp.game.bet.games.share.enums.HistoryActionEnum;

import java.util.ArrayList;
import java.util.List;

public class PlayerOperationInfo {
    private int seatPos;
    private int linkPos;
    private List<HistoryActionEnum> actionList;
    private int card;

    public PlayerOperationInfo() {
        actionList = new ArrayList<>();
    }

    public int getSeatPos() {
        return seatPos;
    }

    public void setSeatPos(int seatPos) {
        this.seatPos = seatPos;
    }

    public void addAction( HistoryActionEnum historyActionEnum )
    {
        if( actionList.indexOf(historyActionEnum) == -1 )
        {
            actionList.add(historyActionEnum);
        }
    }

    public boolean hasAction(HistoryActionEnum historyActionEnum)
    {
        return actionList.indexOf(historyActionEnum) >= 0;
    }

    public int getCard() {
        return card;
    }

    public void setCard(int card) {
        this.card = card;
    }


    public int getLinkPos() {
        return linkPos;
    }

    public void setLinkPos(int linkPos) {
        this.linkPos = linkPos;
    }
}
