package com.wisp.game.bet.recharge.dao.entity;

//import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.Expose;
import com.wisp.core.persistence.DataEntity;

import javax.validation.constraints.NotNull;


public class NodeEntity extends DataEntity
{

    //@JSONField(name = "ParentId")
    private long  ParentId;
    //@JSONField(name = "ParentTree")
    private String ParentTree;
    //@JSONField(name = "Icon")
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
    //@JSONField(name = "LangCn")
    private String LangCn;
    //@JSONField(name = "LangTw")
    private String LangTw;
    //@JSONField(name = "LangUs")
    private String LangUs;
    //@JSONField(name = "Description")
    private String Description;

    @Expose
    //@JSONField(name="TestStrong")
    private String testStrong;

    public NodeEntity()
    {
        this.ParentId = -1;
        this.LangCn = "";
        this.LangTw = "";
        this.LangUs = "";
        this.Description = "";
        this.deleteFlag = 0;
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

    public String getLangTw()
    {
        return LangTw;
    }

    public void setLangTw(String langTw)
    {
        LangTw = langTw;
    }

    public String getLangUs()
    {
        return LangUs;
    }

    public void setLangUs(String langUs)
    {
        LangUs = langUs;
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
}
