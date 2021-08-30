package com.wisp.game.bet.recharge.dao.entity;

//import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.Expose;
import com.wisp.core.persistence.DataEntity;

import javax.validation.constraints.NotNull;


public class NodeEntity extends DataEntity
{
    private long  ParentId;
    private String ParentTree;
    private String Icon;
    //@JSONField(name = "Sort")
    @NotNull
    private int Sort;
    //@JSONField(name = "Name")
    @NotNull( message = "名字不能为空")
    private String Name;
    //@JSONField(name = "Url")
    @NotNull(message = "地址不能为空")
    private String Url;
    private String LangCn;
    private String Description;
    private int IsLib;          //是否是库路径

    @Expose
    //@JSONField(name="TestStrong")
    private String testStrong;

    public NodeEntity()
    {
        this.ParentId = -1;
        this.LangCn = "";
        this.Description = "";
        this.deleteFlag = 0;
        this.IsLib = 0;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public long getParentId()
    {
        return ParentId;
    }

    public void setParentId(long parentId)
    {
        ParentId = parentId;
    }

    public String getParentTree()
    {
        return ParentTree;
    }

    public void setParentTree(String parentTree)
    {
        ParentTree = parentTree;
    }

    public String getIcon()
    {
        return Icon;
    }

    public void setIcon(String icon)
    {
        Icon = icon;
    }

    public int getSort()
    {
        return Sort;
    }

    public void setSort(int sort)
    {
        Sort = sort;
    }

    public String getName()
    {
        return Name;
    }

    public void setName(String name)
    {
        Name = name;
    }

    public String getUrl()
    {
        return Url;
    }

    public void setUrl(String url)
    {
        Url = url;
    }

    public String getLangCn()
    {
        return LangCn;
    }

    public void setLangCn(String langCn)
    {
        LangCn = langCn;
    }

    public String getDescription()
    {
        return Description;
    }

    public void setDescription(String description)
    {
        Description = description;
    }

    public String getTestStrong()
    {
        return testStrong;
    }

    public void setTestStrong(String testStrong)
    {
        this.testStrong = testStrong;
    }

    public int getIsLib()
    {
        return IsLib;
    }

    public void setIsLib(int isLib)
    {
        IsLib = isLib;
    }
}
