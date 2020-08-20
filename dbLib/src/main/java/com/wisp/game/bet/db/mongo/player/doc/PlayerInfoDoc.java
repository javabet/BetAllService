package com.wisp.game.bet.db.mongo.player.doc;

import com.wisp.game.bet.db.mongo.BaseMongoDoc;
import com.wisp.game.bet.sshare.convert.TimeInt;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.io.Serializable;
import java.util.Date;

@Document("PlayerInfo")
public class PlayerInfoDoc extends BaseMongoDoc implements Serializable {

    private String Account;
    private String AccType;
    @Field(targetType = FieldType.INT32) private int AgentId;
    private String ChannelID;
    @Field( targetType = FieldType.DATE_TIME) private Date CreateTime;
    private String CsToken;
    private boolean Delete;
    private String DeviceId;
    private String DevicePlatform;
    @Field(targetType = FieldType.INT32)private int FreeGold;
    @Field(targetType = FieldType.INT32)private int Gold;
    private String IconCustom;
    private boolean IsFormal;
    private boolean IsRobot;
    @Field( targetType = FieldType.DATE_TIME)private Date KickEndTime;
    //@Field( targetType = FieldType.DATE_TIME)
    private TimeInt LastDayTime;
    @Field(targetType = FieldType.INT32)private int LastGameId;
    private String LastIp="";
    @Field( targetType = FieldType.DATE_TIME) private Date LastTime;
    @Field(targetType = FieldType.INT32) private int Level;
    @Field(targetType = FieldType.INT32)private int Sex;
    @Field(targetType = FieldType.INT32) private int PlayerId;
    private String NickName;
    private String Platform;
    private String PlayerName;
    @Field(targetType = FieldType.INT32)private int RechargeNum;
    @Field(targetType = FieldType.INT32)private int Recharged;
    private String RegisterIp;
    @Field( targetType = FieldType.DATE_TIME)private Date RegisterTime;
    @Field(targetType = FieldType.INT32)private int RoomCard;
    @Field(targetType = FieldType.INT32)private int VipExp;
    @Field(targetType = FieldType.INT32)private int VipLevel;
    @Field(targetType = FieldType.INT32)private int PhotoFrameId;

    public PlayerInfoDoc() {
        IconCustom = "";
        PhotoFrameId = 0;
    }

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


    public boolean isDelete() {
        return Delete;
    }

    public void setDelete(boolean delete) {
        Delete = delete;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }

    public String getDevicePlatform() {
        return DevicePlatform;
    }

    public void setDevicePlatform(String devicePlatform) {
        DevicePlatform = devicePlatform;
    }

    public int getFreeGold() {
        return FreeGold;
    }

    public void setFreeGold(int freeGold) {
        FreeGold = freeGold;
    }

    public boolean isFormal() {
        return IsFormal;
    }

    public void setFormal(boolean formal) {
        IsFormal = formal;
    }

    public TimeInt getLastDayTime() {
        return LastDayTime;
    }

    public void setLastDayTime(TimeInt lastDayTime) {
        LastDayTime = lastDayTime;
    }

    public String getPlatform() {
        return Platform;
    }

    public void setPlatform(String platform) {
        Platform = platform;
    }

    public String getPlayerName() {
        return PlayerName;
    }

    public void setPlayerName(String playerName) {
        PlayerName = playerName;
    }

    public int getRechargeNum() {
        return RechargeNum;
    }

    public void setRechargeNum(int rechargeNum) {
        RechargeNum = rechargeNum;
    }

    public int getRecharged() {
        return Recharged;
    }

    public void setRecharged(int recharged) {
        Recharged = recharged;
    }

    public String getRegisterIp() {
        return RegisterIp;
    }

    public void setRegisterIp(String registerIp) {
        RegisterIp = registerIp;
    }

    public Date getRegisterTime() {
        return RegisterTime;
    }

    public void setRegisterTime(Date registerTime) {
        RegisterTime = registerTime;
    }

    public int getRoomCard() {
        return RoomCard;
    }

    public void setRoomCard(int roomCard) {
        RoomCard = roomCard;
    }

    public int getVipExp() {
        return VipExp;
    }

    public void setVipExp(int vipExp) {
        VipExp = vipExp;
    }

    public int getVipLevel() {
        return VipLevel;
    }

    public void setVipLevel(int vipLevel) {
        VipLevel = vipLevel;
    }

    public int getPhotoFrameId() {
        return PhotoFrameId;
    }

    public void setPhotoFrameId(int photoFrameId) {
        PhotoFrameId = photoFrameId;
    }
}
