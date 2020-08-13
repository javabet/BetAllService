package com.wisp.game.bet.db.mongo.games;

import com.wisp.game.bet.db.mongo.BaseMongoDoc;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.List;

@Document(collection = "GameRoomStock")
public class GameRoomStockDoc extends BaseMongoDoc {
    @Field(targetType = FieldType.INT32) private int GameId;
    @Field(targetType = FieldType.INT32) private int RoomId;
    @Field(targetType = FieldType.INT32) private int AgentId;
    @Field(targetType = FieldType.INT32) private int BaseStock;
    @Field(targetType = FieldType.INT32) private int BrightWater;
    @Field(targetType = FieldType.INT32) private int ExtKillPer;
    @Field(targetType = FieldType.INT32) private int InitStock;
    @Field(targetType = FieldType.INT32) private int MaxStock;
    @Field(targetType = FieldType.INT32) private int RelushTime;
    private String RoomName;
    @Field(targetType = FieldType.INT32) private int StockRate;
    private List<Float> StockStage;
    private List<Integer> StrongKill;
    private List<Integer> WeakKill;
    @Field(targetType = FieldType.INT32) private int TemplateId;
    private List<Integer> StockBuff;


    public int getGameId() {
        return GameId;
    }

    public void setGameId(int gameId) {
        GameId = gameId;
    }

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

    public int getBaseStock() {
        return BaseStock;
    }

    public void setBaseStock(int baseStock) {
        BaseStock = baseStock;
    }

    public int getBrightWater() {
        return BrightWater;
    }

    public void setBrightWater(int brightWater) {
        BrightWater = brightWater;
    }

    public int getExtKillPer() {
        return ExtKillPer;
    }

    public void setExtKillPer(int extKillPer) {
        ExtKillPer = extKillPer;
    }

    public int getInitStock() {
        return InitStock;
    }

    public void setInitStock(int initStock) {
        InitStock = initStock;
    }

    public int getMaxStock() {
        return MaxStock;
    }

    public void setMaxStock(int maxStock) {
        MaxStock = maxStock;
    }

    public int getRelushTime() {
        return RelushTime;
    }

    public void setRelushTime(int relushTime) {
        RelushTime = relushTime;
    }

    public String getRoomName() {
        return RoomName;
    }

    public void setRoomName(String roomName) {
        RoomName = roomName;
    }

    public int getStockRate() {
        return StockRate;
    }

    public void setStockRate(int stockRate) {
        StockRate = stockRate;
    }

    public List<Float> getStockStage() {
        return StockStage;
    }

    public void setStockStage(List<Float> stockStage) {
        StockStage = stockStage;
    }

    public List<Integer> getStrongKill() {
        return StrongKill;
    }

    public void setStrongKill(List<Integer> strongKill) {
        StrongKill = strongKill;
    }

    public List<Integer> getWeakKill() {
        return WeakKill;
    }

    public void setWeakKill(List<Integer> weakKill) {
        WeakKill = weakKill;
    }

    public int getTemplateId() {
        return TemplateId;
    }

    public void setTemplateId(int templateId) {
        TemplateId = templateId;
    }

    public List<Integer> getStockBuff() {
        return StockBuff;
    }

    public void setStockBuff(List<Integer> stockBuff) {
        StockBuff = stockBuff;
    }
}
