package com.wisp.game.bet.games.guanyuan.logic;

public class QiangGangContext {
    private int seatPos;    //被抢方

    private int card;   //抢的那张牌

    private int huCnt;  //抢后，被胡的次数


    public int getSeatPos() {
        return seatPos;
    }

    public void setSeatPos(int seatPos) {
        this.seatPos = seatPos;
    }

    public int getCard() {
        return card;
    }

    public void setCard(int card) {
        this.card = card;
    }

    public int getHuCnt() {
        return huCnt;
    }

    public void setHuCnt(int huCnt) {
        this.huCnt = huCnt;
    }
}
