package com.wisp.game.bet.db.mongo.account.info;

import com.wisp.game.bet.db.mongo.BaseMongoDoc;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Document(collection="AccountTable")
@CompoundIndex(name = "Account",def = "{'Account':1}",unique = true)
public class AccountTableDoc extends BaseMongoDoc implements Serializable {

    @MongoId(FieldType.OBJECT_ID)
    private String Id;

    @Indexed(unique = true)
    private String Account;

    @Indexed
    private String ChannelId;

    private int AgentId;


    //@Transient        //表示此值 是暂时的，不存储于数据库中

    @Field(targetType=FieldType.DATE_TIME)
    private Date RegisterTime;

    @Field(targetType=FieldType.DATE_TIME,value = "RegisterTimeNumber")
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


    public Date getRegisterTime() {
        return RegisterTime;
    }

    public void setRegisterTime(Date registerTime) {
        RegisterTime = registerTime;
    }

}
