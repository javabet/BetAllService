package com.wisp.game.bet.recharge.dao.entity;

import com.wisp.core.persistence.DataEntity;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

public class ModeEntity extends DataEntity
{
    private int ParentId;

    @Required()
    private int NodeId;

    @Required()
    @NotNull
    private String Name;

    private String Key;

    private String Md5;

    @Range(min = 1,max = 3,message = "10030")
    private int Type;

    private int Logs;

    private int Sort;

    private String Description;

    public ModeEntity()
    {
        Description = "";
    }

    public int getParentId()
    {
        return ParentId;
    }

    public void setParentId(int parentId)
    {
        ParentId = parentId;
    }

    public int getNodeId()
    {
        return NodeId;
    }

    public void setNodeId(int nodeId)
    {
        NodeId = nodeId;
    }

    public String getName()
    {
        return Name;
    }

    public void setName(String name)
    {
        Name = name;
    }

    public String getKey()
    {
        return Key;
    }

    public void setKey(String key)
    {
        Key = key;
    }

    public String getMd5()
    {
        return Md5;
    }

    public void setMd5(String md5)
    {
        Md5 = md5;
    }


    public int getLogs()
    {
        return Logs;
    }

    public void setLogs(int logs)
    {
        Logs = logs;
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

    public int getSort()
    {
        return Sort;
    }

    public void setSort(int sort)
    {
        Sort = sort;
    }
}
