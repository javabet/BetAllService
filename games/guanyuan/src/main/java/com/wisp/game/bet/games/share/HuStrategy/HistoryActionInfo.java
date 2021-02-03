package com.wisp.game.bet.games.share.HuStrategy;

import com.wisp.game.bet.games.share.enums.HistoryActionEnum;

import java.util.ArrayList;
import java.util.List;

public class HistoryActionInfo {
    private int card;
    private HistoryActionEnum action;
    private int seatPos;
    private int linkedSeatPos;          //被关联的玩家的位置，比如杠牌时，被杠的玩家的
    private boolean isHu;       //是否是胡信息
    private List<Integer> linkCards;        //有需要关联cards时，暂时只与吃有关
    private boolean isZiMo;


    public HistoryActionInfo() {
        card = -1;
        isHu = false;
        linkCards = new ArrayList<>();
        isZiMo = false;
    }

    public int getCard() {
        return card;
    }

    public void setCard(int card) {
        this.card = card;
    }

    public HistoryActionEnum getAction() {
        return action;
    }

    public void setAction(HistoryActionEnum action) {
        this.action = action;
    }


    public int getSeatPos() {
        return seatPos;
    }

    public void setSeatPos(int seatPos) {
        this.seatPos = seatPos;
    }

    public int getLinkedSeatPos() {
        return linkedSeatPos;
    }

    public void setLinkedSeatPos(int linkedSeatPos) {
        this.linkedSeatPos = linkedSeatPos;
    }

    public boolean isHu() {
        return isHu;
    }

    public void setHu(boolean hu) {
        isHu = hu;
    }

    public List<Integer> getLinkCards() {
        return linkCards;
    }

    public void setLinkCards(List<Integer> linkCards) {
        this.linkCards = linkCards;
    }

    public boolean isZiMo() {
        return isZiMo;
    }

    public void setZiMo(boolean ziMo) {
        isZiMo = ziMo;
    }




    public HistoryActionInfo clone()
    {
        HistoryActionInfo historyActionInfo = new HistoryActionInfo();
       historyActionInfo.setSeatPos(seatPos);
       historyActionInfo.setAction(action);
       historyActionInfo.setCard(card);
       historyActionInfo.setLinkedSeatPos(linkedSeatPos);
       historyActionInfo.setHu(isHu);

        return historyActionInfo;
    }
}
