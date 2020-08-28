package com.wisp.game.bet.http.dbdoc;

import com.wisp.game.bet.db.mongo.BaseMongoDoc;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Document("GameApiPayOrder2")
public class GameApiPayOrder2Doc extends BaseMongoDoc {
    private String  Account;
    @Field(targetType = FieldType.INT32) private int PlayerId;
    @Field(targetType = FieldType.INT32) private int Agent;
    private String OrderId;
    private String ChannelId;


    public String getAccount() {
        return Account;
    }

    public void setAccount(String account) {
        Account = account;
    }

    public int getPlayerId() {
        return PlayerId;
    }

    public void setPlayerId(int playerId) {
        PlayerId = playerId;
    }

    public int getAgent() {
        return Agent;
    }

    public void setAgent(int agent) {
        Agent = agent;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getChannelId() {
        return ChannelId;
    }

    public void setChannelId(String channelId) {
        ChannelId = channelId;
    }
}
