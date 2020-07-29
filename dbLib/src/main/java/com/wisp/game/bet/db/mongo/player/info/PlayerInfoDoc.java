package com.wisp.game.bet.db.mongo.player.info;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.io.Serializable;
import java.util.Date;

@Document("PlayerInfo")
public class PlayerInfoDoc implements Serializable {

    private String Account;
    private String AccType;
    private int AgentId;
    @Field( targetType = FieldType.DATE_TIME)
    private int CreateTime;
    private String CsToken;
    private int Gold;
    private String IconCustom;
    private int LastGameId;
    private String LastIp;
    @Field( targetType = FieldType.DATE_TIME)
    private int LastTime;
    private int Level;
    private int Sex;
    private int PlayerId;
    private String NickName;

    public String getAccount() {
        return Account;
    }

    public void setAccount(String account) {
        Account = account;
    }

    public String getAccType() {
        return AccType;
    }

    public void setAccType(String accType) {
        AccType = accType;
    }

    public int getAgentId() {
        return AgentId;
    }

    public void setAgentId(int agentId) {
        AgentId = agentId;
    }

    public int getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(int createTime) {
        CreateTime = createTime;
    }

    public String getCsToken() {
        return CsToken;
    }

    public void setCsToken(String csToken) {
        CsToken = csToken;
    }

    public int getGold() {
        return Gold;
    }

    public void setGold(int gold) {
        Gold = gold;
    }

    public String getIconCustom() {
        return IconCustom;
    }

    public void setIconCustom(String iconCustom) {
        IconCustom = iconCustom;
    }

    public int getLastGameId() {
        return LastGameId;
    }

    public void setLastGameId(int lastGameId) {
        LastGameId = lastGameId;
    }

    public String getLastIp() {
        return LastIp;
    }

    public void setLastIp(String lastIp) {
        LastIp = lastIp;
    }

    public int getLastTime() {
        return LastTime;
    }

    public void setLastTime(int lastTime) {
        LastTime = lastTime;
    }

    public int getLevel() {
        return Level;
    }

    public void setLevel(int level) {
        Level = level;
    }

    public int getSex() {
        return Sex;
    }

    public void setSex(int sex) {
        Sex = sex;
    }

    public int getPlayerId() {
        return PlayerId;
    }

    public void setPlayerId(int playerId) {
        PlayerId = playerId;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }
}
