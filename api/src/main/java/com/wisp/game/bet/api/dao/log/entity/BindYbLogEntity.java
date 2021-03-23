package com.wisp.game.bet.api.dao.log.entity;

import com.wisp.core.persistence.DataEntity;
import com.wisp.core.persistence.MyBatisDao;


public class BindYbLogEntity extends DataEntity
{

    private int cid;

    private int uid;

    private int tm;

    private int sub;

    private int parid;

    private int parcnt;


    public int getCid()
    {
        return cid;
    }

    public void setCid(int cid)
    {
        this.cid = cid;
    }

    public int getUid()
    {
        return uid;
    }

    public void setUid(int uid)
    {
        this.uid = uid;
    }

    public int getTm()
    {
        return tm;
    }

    public void setTm(int tm)
    {
        this.tm = tm;
    }

    public int getSub()
    {
        return sub;
    }

    public void setSub(int sub)
    {
        this.sub = sub;
    }

    public int getParid()
    {
        return parid;
    }

    public void setParid(int parid)
    {
        this.parid = parid;
    }

    public int getParcnt()
    {
        return parcnt;
    }

    public void setParcnt(int parcnt)
    {
        this.parcnt = parcnt;
    }
}
