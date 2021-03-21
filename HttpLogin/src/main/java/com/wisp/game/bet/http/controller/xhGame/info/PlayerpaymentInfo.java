package com.wisp.game.bet.http.controller.xhGame.info;

public class PlayerpaymentInfo
{
    private int PlayerId;
    private int GameOrderId;
    private int PayType;
    private int Amount;
    private String PlayerName;
    private String Ip;
    private String PayPlatform;
    private String ChannelId;
    private int RechargeId;

    public int getPlayerId()
    {
        return PlayerId;
    }

    public void setPlayerId(int playerId)
    {
        PlayerId = playerId;
    }

    public int getGameOrderId()
    {
        return GameOrderId;
    }

    public void setGameOrderId(int gameOrderId)
    {
        GameOrderId = gameOrderId;
    }

    public int getPayType()
    {
        return PayType;
    }

    public void setPayType(int payType)
    {
        PayType = payType;
    }

    public int getAmount()
    {
        return Amount;
    }

    public void setAmount(int amount)
    {
        Amount = amount;
    }

    public String getPlayerName()
    {
        return PlayerName;
    }

    public void setPlayerName(String playerName)
    {
        PlayerName = playerName;
    }

    public String getIp()
    {
        return Ip;
    }

    public void setIp(String ip)
    {
        Ip = ip;
    }

    public String getPayPlatform()
    {
        return PayPlatform;
    }

    public void setPayPlatform(String payPlatform)
    {
        PayPlatform = payPlatform;
    }

    public String getChannelId()
    {
        return ChannelId;
    }

    public void setChannelId(String channelId)
    {
        ChannelId = channelId;
    }

    public int getRechargeId()
    {
        return RechargeId;
    }

    public void setRechargeId(int rechargeId)
    {
        RechargeId = rechargeId;
    }
}
