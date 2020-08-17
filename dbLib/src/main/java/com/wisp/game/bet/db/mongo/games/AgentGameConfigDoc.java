package com.wisp.game.bet.db.mongo.games;

import com.wisp.game.bet.db.mongo.BaseMongoDoc;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.util.List;

@Document("AgentGameConfig")
public class AgentGameConfigDoc extends BaseMongoDoc {

    private static final long serialVersionUID = 4168624154753698643L;

    @Field(targetType = FieldType.INT32) private int AgentId;
    @Field(targetType = FieldType.INT32)private int GameId;
    private List<Integer> CustomList;
    @Field( targetType = FieldType.DOUBLE) private double EarningRate;
    private boolean IsHot;
    private boolean isPowerful;

    public int getAgentId() {
        return AgentId;
    }

    public void setAgentId(int agentId) {
        AgentId = agentId;
    }

    public int getGameId() {
        return GameId;
    }

    public void setGameId(int gameId) {
        GameId = gameId;
    }

    public List<Integer> getCustomList() {
        return CustomList;
    }

    public void setCustomList(List<Integer> customList) {
        CustomList = customList;
    }

    public double getEarningRate() {
        return EarningRate;
    }

    public void setEarningRate(double earningRate) {
        EarningRate = earningRate;
    }

    public boolean isHot() {
        return IsHot;
    }

    public void setHot(boolean hot) {
        IsHot = hot;
    }

    public boolean isPowerful() {
        return isPowerful;
    }

    public void setPowerful(boolean powerful) {
        isPowerful = powerful;
    }
}
