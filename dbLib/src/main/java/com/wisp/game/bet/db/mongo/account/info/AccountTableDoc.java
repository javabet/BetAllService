package com.wisp.game.bet.db.mongo.account.info;

import com.wisp.game.bet.db.mongo.BaseMongoDoc;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;

@Document(collection="AccountTable")
public class AccountTableDoc extends BaseMongoDoc implements Serializable {

    @MongoId(FieldType.OBJECT_ID)
    private String Id;
    private String Account;
    private String ChannelId;

    @Transient
    private int AgentId;

    //@Transient        //表示此值 是暂时的，不存储于数据库中

    //@Field(targetType=FieldType.DATE_TIME)
    //private Date RegisterTime;

    @Field(targetType=FieldType.DATE_TIME,value = "RegisterTime")
    private Long RegisterTimeNumber;

    public void setId(String id) {
        Id = id;
    }

    public String getId() {
        return Id;
    }

    @Field(targetType = FieldType.STRING)
    public String getAccount() {
        return Account;
    }

    public void setAccount(String account) {
        Account = account;
    }


    public String getChannelId() {
        return ChannelId;
    }

    public void setChannelId(String channelId) {
        ChannelId = channelId;
    }

    public int getAgentId() {
        return AgentId;
    }

    public void setAgentId(int agentId) {
        AgentId = agentId;
    }



    public Long getRegisterTimeNumber() {
        return RegisterTimeNumber;
    }

    public void setRegisterTimeNumber(Long registerTimeNumber) {
        RegisterTimeNumber = registerTimeNumber;
    }
}
