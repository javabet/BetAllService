package com.wisp.game.bet.db.mongo.account.doc;

import com.wisp.game.bet.db.mongo.BaseMongoDoc;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.*;

import java.io.Serializable;
import java.util.Date;

@Document(collection="AccountTable")
@CompoundIndex(name = "Account",def = "{'Account':1}",unique = true)
public class AccountTableDoc extends BaseMongoDoc implements Serializable {

    private String RandKey;
    private long LastTime;
    private String LastIp;
    private String ClientChannelId;
    private String AccPhone;
    @Indexed(unique = true)
    private String Account;
    private String AccPwd;
    private String AccDev;
    private String DeviceId;
    @Field(targetType=FieldType.DATE_TIME)
    private Date RegisterTime;
    private String RegisterIp;
    private String ChannelId;
    @Field(targetType = FieldType.INT32) private int Level;
    private String Platform;
    private String DevicePlatform;
    @Field(targetType = FieldType.INT32)private int PackageId;
    @Field(targetType = FieldType.INT32)private int AgentId;
    private boolean LocationDisable;


    public String getRandKey() {
        return RandKey;
    }

    public void setRandKey(String randKey) {
        RandKey = randKey;
    }

    public long getLastTime() {
        return LastTime;
    }

    public void setLastTime(long lastTime) {
        LastTime = lastTime;
    }

    public String getLastIp() {
        return LastIp;
    }

    public void setLastIp(String lastIp) {
        LastIp = lastIp;
    }

    public String getClientChannelId() {
        return ClientChannelId;
    }

    public void setClientChannelId(String clientChannelId) {
        ClientChannelId = clientChannelId;
    }

    public String getAccPhone() {
        return AccPhone;
    }

    public void setAccPhone(String accPhone) {
        AccPhone = accPhone;
    }

    public String getAccount() {
        return Account;
    }

    public void setAccount(String account) {
        Account = account;
    }

    public String getAccPwd() {
        return AccPwd;
    }

    public void setAccPwd(String accPwd) {
        AccPwd = accPwd;
    }

    public String getAccDev() {
        return AccDev;
    }

    public void setAccDev(String accDev) {
        AccDev = accDev;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }

    public Date getRegisterTime() {
        return RegisterTime;
    }

    public void setRegisterTime(Date registerTime) {
        RegisterTime = registerTime;
    }

    public String getRegisterIp() {
        return RegisterIp;
    }

    public void setRegisterIp(String registerIp) {
        RegisterIp = registerIp;
    }

    public String getChannelId() {
        return ChannelId;
    }

    public void setChannelId(String channelId) {
        ChannelId = channelId;
    }

    public int getLevel() {
        return Level;
    }

    public void setLevel(int level) {
        Level = level;
    }

    public String getPlatform() {
        return Platform;
    }

    public void setPlatform(String platform) {
        Platform = platform;
    }

    public String getDevicePlatform() {
        return DevicePlatform;
    }

    public void setDevicePlatform(String devicePlatform) {
        DevicePlatform = devicePlatform;
    }

    public int getPackageId() {
        return PackageId;
    }

    public void setPackageId(int packageId) {
        PackageId = packageId;
    }

    public int getAgentId() {
        return AgentId;
    }

    public void setAgentId(int agentId) {
        AgentId = agentId;
    }

    public boolean isLocationDisable() {
        return LocationDisable;
    }

    public void setLocationDisable(boolean locationDisable) {
        LocationDisable = locationDisable;
    }
}
