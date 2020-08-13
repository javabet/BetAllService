package com.wisp.game.bet.games.share.config;

import java.util.List;

public class RMStockConfigData {
    private int mRoomID; //房间id

    private String mRoomName; //房间名

    private int mBaseStock; //基础库存

    private int mInitStock; //初始库存

    private int mStockRate; //盈利(万分之一)

    private List<Float> mStockStage; //库存阶段（库存在某个阶段对于不同收放分）

    private List<Integer> mStockBuff; //库存增益(大于0放分，小于0收分，等于0随机)

    private int mBrightWater; //明抽

    private int mMaxStock; //最大库存

    private int mRelushTime; //刷新时间

    private List<Integer> mStrongKill; //强杀概率

    private List<Integer> mWeakKill; //弱杀概率


    public int getmRoomID() {
        return mRoomID;
    }

    public void setmRoomID(int mRoomID) {
        this.mRoomID = mRoomID;
    }

    public String getmRoomName() {
        return mRoomName;
    }

    public void setmRoomName(String mRoomName) {
        this.mRoomName = mRoomName;
    }

    public int getmBaseStock() {
        return mBaseStock;
    }

    public void setmBaseStock(int mBaseStock) {
        this.mBaseStock = mBaseStock;
    }

    public int getmInitStock() {
        return mInitStock;
    }

    public void setmInitStock(int mInitStock) {
        this.mInitStock = mInitStock;
    }

    public int getmStockRate() {
        return mStockRate;
    }

    public void setmStockRate(int mStockRate) {
        this.mStockRate = mStockRate;
    }

    public List<Float> getmStockStage() {
        return mStockStage;
    }

    public void setmStockStage(List<Float> mStockStage) {
        this.mStockStage = mStockStage;
    }

    public List<Integer> getmStockBuff() {
        return mStockBuff;
    }

    public void setmStockBuff(List<Integer> mStockBuff) {
        this.mStockBuff = mStockBuff;
    }

    public int getmBrightWater() {
        return mBrightWater;
    }

    public void setmBrightWater(int mBrightWater) {
        this.mBrightWater = mBrightWater;
    }

    public int getmMaxStock() {
        return mMaxStock;
    }

    public void setmMaxStock(int mMaxStock) {
        this.mMaxStock = mMaxStock;
    }

    public int getmRelushTime() {
        return mRelushTime;
    }

    public void setmRelushTime(int mRelushTime) {
        this.mRelushTime = mRelushTime;
    }

    public List<Integer> getmStrongKill() {
        return mStrongKill;
    }

    public void setmStrongKill(List<Integer> mStrongKill) {
        this.mStrongKill = mStrongKill;
    }

    public List<Integer> getmWeakKill() {
        return mWeakKill;
    }

    public void setmWeakKill(List<Integer> mWeakKill) {
        this.mWeakKill = mWeakKill;
    }
}
