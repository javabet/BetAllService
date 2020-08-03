package com.wisp.game.bet.db.mongo.account.info;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.util.Date;

@Document("ServerInfo")
public class ServerInfoDoc implements Serializable {
    @MongoId(FieldType.OBJECT_ID)
    private String _id;

    private String Account;

    private int GateId;

    private int IsConnect;

    private Date LastTime;

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
