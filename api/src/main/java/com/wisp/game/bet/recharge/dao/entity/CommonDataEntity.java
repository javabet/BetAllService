package com.wisp.game.bet.recharge.dao.entity;

import com.wisp.core.persistence.DataEntity;

public class CommonDataEntity extends DataEntity
{
    private long AdminId;//所有人
    private long CreateAdminId;//创建人

    public long getAdminId()
    {
        return AdminId;
    }

    public void setAdminId(long adminId)
    {
        AdminId = adminId;
    }

    public long getCreateAdminId()
    {
        return CreateAdminId;
    }

    public void setCreateAdminId(long createAdminId)
    {
        CreateAdminId = createAdminId;
    }
}
