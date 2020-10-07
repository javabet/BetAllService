package com.wisp.game.bet.http.controller.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;


public class ReqBalanceDto {

    @JsonProperty( value = "AgentId")
    private int AgentId;

    @JsonProperty( value = "ChannelId")
    private  String ChannelId;

    @JsonProperty( value = "PlayerId")
    private int PlayerId;

    @JsonProperty( value = "UserName")
    private String UserName;

    @JsonProperty( value = "Balance")
    private double Balance;

    @JsonProperty( value = "OrderId")
    private String OrderId;

    public ReqBalanceDto() {
    }

    public int getAgentId() {
        return AgentId;
    }

    public void setAgentId(int agentId) {
        AgentId = agentId;
    }

    public String getChannelId() {
        return ChannelId;
    }

    public void setChannelId(String channelId) {
        ChannelId = channelId;
    }

    public int getPlayerId() {
        return PlayerId;
    }

    public void setPlayerId(int playerId) {
        PlayerId = playerId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public double getBalance() {
        return Balance;
    }

    public void setBalance(double balance) {
        Balance = balance;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

}
