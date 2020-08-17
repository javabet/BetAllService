package com.wisp.game.bet.db.mongo.games;

import com.wisp.game.bet.db.mongo.games.Baccara.info.HistoryItemChildDoc;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.List;

/**
 * 使用StockCtrlXX时，每个子游戏的名字不同，所以不能指定其collection
 */

public class GameRoomDoc {
    @Field(targetType = FieldType.INT32) private int RoomId;
    @Field(targetType = FieldType.INT32)private int AgentId;
    @Field(targetType = FieldType.DOUBLE)private double AwardFour;
    @Field(targetType = FieldType.DOUBLE)private double AwardFullHouse;
    @Field(targetType = FieldType.DOUBLE)private double AwardPool;
    @Field(targetType = FieldType.DOUBLE)private double AwardThree;
    @Field(targetType = FieldType.DOUBLE) private double AwardTwoPair;
    @Field(targetType = FieldType.DOUBLE)private  double BrightRate;
    @Field(targetType = FieldType.INT64)private long BrightWater;
    @Field(targetType = FieldType.INT64)private long EnterCount;
    private List<HistoryItemChildDoc> HistoryList;
    @Field(targetType = FieldType.INT64)private long OtherStock;
    @Field(targetType = FieldType.INT64)private long PlayerCharge;
    @Field(targetType = FieldType.INT64)private boolean ResetStock;
    @Field(targetType = FieldType.INT64)private long RobIncome;
    @Field(targetType = FieldType.INT64)private long RobOutcome;
    @Field(targetType = FieldType.INT64)private long RoomIncome;
    @Field(targetType = FieldType.INT64)private long RoomOutcome;
    @Field(targetType = FieldType.INT64)private long RoomStock;


    public int getRoomId() {
        return RoomId;
    }

    public void setRoomId(int roomId) {
        RoomId = roomId;
    }

    public int getAgentId() {
        return AgentId;
    }

    public void setAgentId(int agentId) {
        AgentId = agentId;
    }

    public double getAwardFour() {
        return AwardFour;
    }

    public void setAwardFour(double awardFour) {
        AwardFour = awardFour;
    }

    public double getAwardFullHouse() {
        return AwardFullHouse;
    }

    public void setAwardFullHouse(double awardFullHouse) {
        AwardFullHouse = awardFullHouse;
    }

    public double getAwardPool() {
        return AwardPool;
    }

    public void setAwardPool(double awardPool) {
        AwardPool = awardPool;
    }

    public double getAwardThree() {
        return AwardThree;
    }

    public void setAwardThree(double awardThree) {
        AwardThree = awardThree;
    }

    public double getAwardTwoPair() {
        return AwardTwoPair;
    }

    public void setAwardTwoPair(double awardTwoPair) {
        AwardTwoPair = awardTwoPair;
    }

    public double getBrightRate() {
        return BrightRate;
    }

    public void setBrightRate(double brightRate) {
        BrightRate = brightRate;
    }

    public long getBrightWater() {
        return BrightWater;
    }

    public void setBrightWater(long brightWater) {
        BrightWater = brightWater;
    }

    public long getEnterCount() {
        return EnterCount;
    }

    public void setEnterCount(long enterCount) {
        EnterCount = enterCount;
    }

    public List<HistoryItemChildDoc> getHistoryList() {
        return HistoryList;
    }

    public void setHistoryList(List<HistoryItemChildDoc> historyList) {
        HistoryList = historyList;
    }

    public long getOtherStock() {
        return OtherStock;
    }

    public void setOtherStock(long otherStock) {
        OtherStock = otherStock;
    }

    public long getPlayerCharge() {
        return PlayerCharge;
    }

    public void setPlayerCharge(long playerCharge) {
        PlayerCharge = playerCharge;
    }

    public boolean isResetStock() {
        return ResetStock;
    }

    public void setResetStock(boolean resetStock) {
        ResetStock = resetStock;
    }

    public long getRobIncome() {
        return RobIncome;
    }

    public void setRobIncome(long robIncome) {
        RobIncome = robIncome;
    }

    public long getRobOutcome() {
        return RobOutcome;
    }

    public void setRobOutcome(long robOutcome) {
        RobOutcome = robOutcome;
    }

    public long getRoomIncome() {
        return RoomIncome;
    }

    public void setRoomIncome(long roomIncome) {
        RoomIncome = roomIncome;
    }

    public long getRoomOutcome() {
        return RoomOutcome;
    }

    public void setRoomOutcome(long roomOutcome) {
        RoomOutcome = roomOutcome;
    }

    public long getRoomStock() {
        return RoomStock;
    }

    public void setRoomStock(long roomStock) {
        RoomStock = roomStock;
    }

}
