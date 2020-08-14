package com.wisp.game.bet.games.share.config;

import java.io.Serializable;
import java.util.List;

public class RMConfigData implements Serializable {
    private static final long serialVersionUID = 8633988825916589390L;
    private int mRoomID; //房间id

    private String mRoomName; //房间名

    private int mGoldCondition; //金币条件

    private int mBetCondition; //下注条件

    private int mBaseGold; //底分

    private int mVipCondition; //vip条件

    private int mTableCount; //桌子数

    private boolean mIsOpen; //是否开放

    private List<Integer> mWeightList; //砝码列表

    private int mBankerCondition; //上庄条件(金币)

    private int mAutoLeaveBanker; //强制下庄条件

    private int mLeaveBankerCost; //提前下庄花费

    private int mFirstBankerCost; //抢庄花费

    private int mAddBankerCost; //每次抢庄竞价累计值

    private List<Integer> mBetLimit; //盘口限红

    private List<Integer> mBetRange; //限红(选房显示)

    private List<Integer> mCustomList; //自定义筹码

    private List<Integer> mPlatList; //人数值参数

    private int mFreeGold; //试玩金额

    private List<String> mBetNames; //下注区名

    private int mPlayerMaxCount; //房间最大人数限制

    private int mCarryRestriction; //最大携带

    private int mSmallBlind; //小盲

    private int mBigBlind; //大盲

    private String mRoomIDTxt; //房间编号

    private int mForceLeaveGold; //强制下桌金额

    private int mBotMinGold; //机器人最小金币

    private int mBotMaxGold; //机器人最大金币

    private int mRoomType; //房间类型

    private int mMaxAnte; //单注封顶

    private int mLevelCondition; //等级条件

    private int mPowerParam; //能量系数

    private int mBuyPowerCost; //购买能量消耗

    private int mMissileCost; //导弹消耗

    private boolean mCanGetExp; //能获取经验

    private boolean mCheckOpenRate; //检查倍率

    private boolean mFreeItem; //免费道具

    private int mEveryLeopardLimit; //任意豹子押注上限

    private int mLeopardLimit; //豹子押注上限

    private List<Integer> mOdds; //赔率规则(26种)

    private int mProfitRate; //盈利率低于该数值动态调整

    private int mProfitRateCheckIntervaltime; //每个多长时间检测一下盈利率 单位分钟

    private List<Integer> mRatePoolList; //倍率

    private int mRoomNameType; //对战类房间类型

    private int mHuaGold; //花分

    private String mGameModel; //麻将游戏类型

    private int mRoomParam1; //通用房间参数1，具体用途取决于各自游戏


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

    public int getmGoldCondition() {
        return mGoldCondition;
    }

    public void setmGoldCondition(int mGoldCondition) {
        this.mGoldCondition = mGoldCondition;
    }

    public int getmBetCondition() {
        return mBetCondition;
    }

    public void setmBetCondition(int mBetCondition) {
        this.mBetCondition = mBetCondition;
    }

    public int getmBaseGold() {
        return mBaseGold;
    }

    public void setmBaseGold(int mBaseGold) {
        this.mBaseGold = mBaseGold;
    }

    public int getmVipCondition() {
        return mVipCondition;
    }

    public void setmVipCondition(int mVipCondition) {
        this.mVipCondition = mVipCondition;
    }

    public int getmTableCount() {
        return mTableCount;
    }

    public void setmTableCount(int mTableCount) {
        this.mTableCount = mTableCount;
    }

    public boolean getmIsOpen() {
        return mIsOpen;
    }

    public void setmIsOpen(boolean mIsOpen) {
        this.mIsOpen = mIsOpen;
    }

    public List<Integer> getmWeightList() {
        return mWeightList;
    }

    public void setmWeightList(List<Integer> mWeightList) {
        this.mWeightList = mWeightList;
    }

    public int getmBankerCondition() {
        return mBankerCondition;
    }

    public void setmBankerCondition(int mBankerCondition) {
        this.mBankerCondition = mBankerCondition;
    }

    public int getmAutoLeaveBanker() {
        return mAutoLeaveBanker;
    }

    public void setmAutoLeaveBanker(int mAutoLeaveBanker) {
        this.mAutoLeaveBanker = mAutoLeaveBanker;
    }

    public int getmLeaveBankerCost() {
        return mLeaveBankerCost;
    }

    public void setmLeaveBankerCost(int mLeaveBankerCost) {
        this.mLeaveBankerCost = mLeaveBankerCost;
    }

    public int getmFirstBankerCost() {
        return mFirstBankerCost;
    }

    public void setmFirstBankerCost(int mFirstBankerCost) {
        this.mFirstBankerCost = mFirstBankerCost;
    }

    public int getmAddBankerCost() {
        return mAddBankerCost;
    }

    public void setmAddBankerCost(int mAddBankerCost) {
        this.mAddBankerCost = mAddBankerCost;
    }

    public List<Integer> getmBetLimit() {
        return mBetLimit;
    }

    public void setmBetLimit(List<Integer> mBetLimit) {
        this.mBetLimit = mBetLimit;
    }

    public List<Integer> getmBetRange() {
        return mBetRange;
    }

    public void setmBetRange(List<Integer> mBetRange) {
        this.mBetRange = mBetRange;
    }

    public List<Integer> getmCustomList() {
        return mCustomList;
    }

    public void setmCustomList(List<Integer> mCustomList) {
        this.mCustomList = mCustomList;
    }

    public List<Integer> getmPlatList() {
        return mPlatList;
    }

    public void setmPlatList(List<Integer> mPlatList) {
        this.mPlatList = mPlatList;
    }

    public int getmFreeGold() {
        return mFreeGold;
    }

    public void setmFreeGold(int mFreeGold) {
        this.mFreeGold = mFreeGold;
    }

    public List<String> getmBetNames() {
        return mBetNames;
    }

    public void setmBetNames(List<String> mBetNames) {
        this.mBetNames = mBetNames;
    }

    public int getmPlayerMaxCount() {
        return mPlayerMaxCount;
    }

    public void setmPlayerMaxCount(int mPlayerMaxCount) {
        this.mPlayerMaxCount = mPlayerMaxCount;
    }

    public int getmCarryRestriction() {
        return mCarryRestriction;
    }

    public void setmCarryRestriction(int mCarryRestriction) {
        this.mCarryRestriction = mCarryRestriction;
    }

    public int getmSmallBlind() {
        return mSmallBlind;
    }

    public void setmSmallBlind(int mSmallBlind) {
        this.mSmallBlind = mSmallBlind;
    }

    public int getmBigBlind() {
        return mBigBlind;
    }

    public void setmBigBlind(int mBigBlind) {
        this.mBigBlind = mBigBlind;
    }

    public String getmRoomIDTxt() {
        return mRoomIDTxt;
    }

    public void setmRoomIDTxt(String mRoomIDTxt) {
        this.mRoomIDTxt = mRoomIDTxt;
    }

    public int getmForceLeaveGold() {
        return mForceLeaveGold;
    }

    public void setmForceLeaveGold(int mForceLeaveGold) {
        this.mForceLeaveGold = mForceLeaveGold;
    }

    public int getmBotMinGold() {
        return mBotMinGold;
    }

    public void setmBotMinGold(int mBotMinGold) {
        this.mBotMinGold = mBotMinGold;
    }

    public int getmBotMaxGold() {
        return mBotMaxGold;
    }

    public void setmBotMaxGold(int mBotMaxGold) {
        this.mBotMaxGold = mBotMaxGold;
    }

    public int getmRoomType() {
        return mRoomType;
    }

    public void setmRoomType(int mRoomType) {
        this.mRoomType = mRoomType;
    }

    public int getmMaxAnte() {
        return mMaxAnte;
    }

    public void setmMaxAnte(int mMaxAnte) {
        this.mMaxAnte = mMaxAnte;
    }

    public int getmLevelCondition() {
        return mLevelCondition;
    }

    public void setmLevelCondition(int mLevelCondition) {
        this.mLevelCondition = mLevelCondition;
    }

    public int getmPowerParam() {
        return mPowerParam;
    }

    public void setmPowerParam(int mPowerParam) {
        this.mPowerParam = mPowerParam;
    }

    public int getmBuyPowerCost() {
        return mBuyPowerCost;
    }

    public void setmBuyPowerCost(int mBuyPowerCost) {
        this.mBuyPowerCost = mBuyPowerCost;
    }

    public int getmMissileCost() {
        return mMissileCost;
    }

    public void setmMissileCost(int mMissileCost) {
        this.mMissileCost = mMissileCost;
    }

    public boolean getmCanGetExp() {
        return mCanGetExp;
    }

    public void setmCanGetExp(boolean mCanGetExp) {
        this.mCanGetExp = mCanGetExp;
    }

    public boolean getmCheckOpenRate() {
        return mCheckOpenRate;
    }

    public void setmCheckOpenRate(boolean mCheckOpenRate) {
        this.mCheckOpenRate = mCheckOpenRate;
    }

    public boolean getmFreeItem() {
        return mFreeItem;
    }

    public void setmFreeItem(boolean mFreeItem) {
        this.mFreeItem = mFreeItem;
    }

    public int getmEveryLeopardLimit() {
        return mEveryLeopardLimit;
    }

    public void setmEveryLeopardLimit(int mEveryLeopardLimit) {
        this.mEveryLeopardLimit = mEveryLeopardLimit;
    }

    public int getmLeopardLimit() {
        return mLeopardLimit;
    }

    public void setmLeopardLimit(int mLeopardLimit) {
        this.mLeopardLimit = mLeopardLimit;
    }

    public List<Integer> getmOdds() {
        return mOdds;
    }

    public void setmOdds(List<Integer> mOdds) {
        this.mOdds = mOdds;
    }

    public int getmProfitRate() {
        return mProfitRate;
    }

    public void setmProfitRate(int mProfitRate) {
        this.mProfitRate = mProfitRate;
    }

    public int getmProfitRateCheckIntervaltime() {
        return mProfitRateCheckIntervaltime;
    }

    public void setmProfitRateCheckIntervaltime(int mProfitRateCheckIntervaltime) {
        this.mProfitRateCheckIntervaltime = mProfitRateCheckIntervaltime;
    }

    public List<Integer> getmRatePoolList() {
        return mRatePoolList;
    }

    public void setmRatePoolList(List<Integer> mRatePoolList) {
        this.mRatePoolList = mRatePoolList;
    }

    public int getmRoomNameType() {
        return mRoomNameType;
    }

    public void setmRoomNameType(int mRoomNameType) {
        this.mRoomNameType = mRoomNameType;
    }

    public int getmHuaGold() {
        return mHuaGold;
    }

    public void setmHuaGold(int mHuaGold) {
        this.mHuaGold = mHuaGold;
    }

    public String getmGameModel() {
        return mGameModel;
    }

    public void setmGameModel(String mGameModel) {
        this.mGameModel = mGameModel;
    }

    public int getmRoomParam1() {
        return mRoomParam1;
    }

    public void setmRoomParam1(int mRoomParam1) {
        this.mRoomParam1 = mRoomParam1;
    }
}
