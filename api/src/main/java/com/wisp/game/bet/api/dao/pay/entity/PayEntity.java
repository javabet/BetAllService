package com.wisp.game.bet.api.dao.pay.entity;

import com.wisp.core.persistence.DataEntity;

public class PayEntity  extends DataEntity
{
    private int uid;

    private int paypoint;

    private int tm;

    private String token;

    public int getUid()
    {
        return uid;
    }

    public void setUid(int uid)
    {
        this.uid = uid;
    }

    public int getPaypoint()
    {
        return paypoint;
    }

    public void setPaypoint(int paypoint)
    {
        this.paypoint = paypoint;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public int getTm()
    {
        return tm;
    }

    public void setTm(int tm)
    {
        this.tm = tm;
    }
}
