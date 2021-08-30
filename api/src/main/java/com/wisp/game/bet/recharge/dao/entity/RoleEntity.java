package com.wisp.game.bet.recharge.dao.entity;

import com.wisp.core.persistence.DataEntity;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class RoleEntity  extends CommonDataEntity
{
    @Required
    private String Name;

    @Required
    @NotNull
    @NotEmpty
    private String Permission;


    private String Node;

    private String System;

    @Required
    private String Description;

    private int Type;

    public RoleEntity()
    {
        System = "";
    }

    public String getName()
    {
        return Name;
    }

    public void setName(String name)
    {
        Name = name;
    }

    public String getPermission()
    {
        return Permission;
    }

    public void setPermission(String permission)
    {
        Permission = permission;
    }

    public String getNode()
    {
        return Node;
    }

    public void setNode(String node)
    {
        Node = node;
    }

    public String getSystem()
    {
        return System;
    }

    public void setSystem(String system)
    {
        System = system;
    }

    public String getDescription()
    {
        return Description;
    }

    public void setDescription(String description)
    {
        Description = description;
    }

    public int getType()
    {
        return Type;
    }

    public void setType(int type)
    {
        Type = type;
    }

}
