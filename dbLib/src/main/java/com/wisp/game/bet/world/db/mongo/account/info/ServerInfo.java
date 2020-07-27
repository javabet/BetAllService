package com.wisp.game.bet.world.db.mongo.account.info;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;

@Document("ServerInfo")
public class ServerInfo implements Serializable {
    @MongoId(FieldType.OBJECT_ID)
    private String _id;

    private String Account;

    private int GateId;

    private int IsConnect;

    @Field(targetType=FieldType.DATE_TIME)
    private int LastTime;

    private int WorldId;


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

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

    public int getLastTime() {
        return LastTime;
    }

    public void setLastTime(int lastTime) {
        LastTime = lastTime;
    }

    public int getWorldId() {
        return WorldId;
    }

    public void setWorldId(int worldId) {
        WorldId = worldId;
    }
}
