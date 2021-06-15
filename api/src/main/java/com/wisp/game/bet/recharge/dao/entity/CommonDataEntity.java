package com.wisp.game.bet.recharge.dao.entity;

import com.wisp.core.persistence.DataEntity;

public class CommonDataEntity extends DataEntity
{
    private int AdminId;//所有人
    private int CreateAdminId;//创建人

    public int getAdminId()
    {
        return AdminId;
    }

    public void setAdminId(int adminId)
    {
        AdminId = adminId;
    }

    public int getCreateAdminId()
    {
        return CreateAdminId;
    }

    public void setCreateAdminId(int createAdminId)
    {
        CreateAdminId = createAdminId;
    }
}
