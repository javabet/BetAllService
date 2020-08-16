package com.wisp.game.bet.db.mongo.account.doc;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.util.Date;

@Document("ServerInfo")
public class ServerInfoDoc implements Serializable {


    private String Account;

    @Field( targetType = FieldType.INT32)
    private int GateId;

    @Field( targetType = FieldType.INT32)
    private int IsConnect;

    private Date LastTime;

    @Field( targetType = FieldType.INT32)
    private int WorldId;


    public String getAccount() {
        return Account;
    }

    public void setAccount(String account) {
        Account = account;
    }

    public int getGateId() {
        return GateId;
    }

    public void setGateId(int gateId) {
        GateId = gateId;
    }

    public int getIsConnect() {
        return IsConnect;
    }

    public void setIsConnect(int isConnect) {
        IsConnect = isConnect;
    }

    public Date getLastTime() {
        return LastTime;
    }

    public void setLastTime(Date lastTime) {
        LastTime = lastTime;
    }

    public int getWorldId() {
        return WorldId;
    }

    public void setWorldId(int worldId) {
        WorldId = worldId;
    }
}
