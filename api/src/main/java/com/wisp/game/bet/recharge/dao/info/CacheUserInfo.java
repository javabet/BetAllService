package com.wisp.game.bet.recharge.dao.info;

import java.io.Serializable;

public class CacheUserInfo implements Serializable
{

    private static final long serialVersionUID = 8223997628795894518L;

    private long adminId;

    public long getAdminId()
    {

        return adminId;
    }

    public void setAdminId(long adminId)
    {
        this.adminId = adminId;
    }
}
