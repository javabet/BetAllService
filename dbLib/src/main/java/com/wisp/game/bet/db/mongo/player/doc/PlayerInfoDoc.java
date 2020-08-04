package com.wisp.game.bet.db.mongo.player.doc;

import com.wisp.game.bet.db.mongo.BaseMongoDoc;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.io.Serializable;
import java.util.Date;

@Document("PlayerInfo")
public class PlayerInfoDoc extends BaseMongoDoc implements Serializable {

    private String Account;
    private String AccType;
    @Field(targetType = FieldType.INT32)
    private int AgentId;
    private String ChannelID;
    @Field( targetType = FieldType.DATE_TIME)
    private Date CreateTime;
    private String CsToken;
    @Field(targetType = FieldType.INT32)
    private int Gold;
    private String IconCustom;
    @Field(targetType = FieldType.INT32)
    private int LastGameId;
    private String LastIp;
    @Field( targetType = FieldType.DATE_TIME)
    private Date LastTime;
    @Field(targetType = FieldType.INT32)
    private int Level;
    @Field(targetType = FieldType.INT32)
    private int Sex;
    @Field(targetType = FieldType.INT32)
    private int PlayerId;
    private String NickName;
    private boolean IsRobot;
    @Field( targetType = FieldType.DATE_TIME)
    private Date KickEndTime;

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

    public boolean isRobot() {
        return IsRobot;
    }

    public void setRobot(boolean robot) {
        IsRobot = robot;
    }

    public Date getKickEndTime() {
        return KickEndTime;
    }

    public void setKickEndTime(Date kickEndTime) {
        KickEndTime = kickEndTime;
    }

    public String getChannelID() {
        return ChannelID;
    }

    public void setChannelID(String channelID) {
        ChannelID = channelID;
    }

    public Date getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(Date createTime) {
        CreateTime = createTime;
    }

    public Date getLastTime() {
        return LastTime;
    }

    public void setLastTime(Date lastTime) {
        LastTime = lastTime;
    }
}
