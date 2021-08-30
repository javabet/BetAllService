package com.wisp.game.bet.recharge.dao.entity;

import com.wisp.core.persistence.DataEntity;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class AdminEntity extends DataEntity
{
    @Required
    @NotNull( message = "用户名不能为空")
    @Length(min = 2,max = 20,message = "长度需要在2到20之间")
    private String Username;//(用户名，登陆账户)
    @Required
    @Length(min = 6,max = 50,message = "长度不合适")
    private String Password;//用户密码
    private String Role;//角色
    private String CreateRole;//可创建角色列表（,分割）
    @Length(max = 20,message = "100019")
    private String Name;    //真实姓名
    private int Sex;        //性别 1：男 2：女
    private String LoginIp;//登陆IP
    //@Range(min = 1,max = 2,message = "10018")
    private int Status;     //(1:启用 2：停用)
    private int ParentId;       //父节点
    private String ParentTree;  //父节点树
    private int LoginTime;      //登陆时间
    private String Mobile;         //电话
    private String Note;        //备注

    public String getUsername()
    {
        return Username;
    }

    public void setUsername(String username)
    {
        Username = username;
    }

    public String getPassword()
    {
        return Password;
    }

    public void setPassword(String password)
    {
        Password = password;
    }

    public String getRole()
    {
        return Role;
    }

    public void setRole(String role)
    {
        Role = role;
    }

    public String getCreateRole()
    {
        return CreateRole;
    }

    public void setCreateRole(String createRole)
    {
        CreateRole = createRole;
    }

    public String getName()
    {
        return Name;
    }

    public void setName(String name)
    {
        Name = name;
    }

    public int getSex()
    {
        return Sex;
    }

    public void setSex(int sex)
    {
        this.Sex = sex;
    }

    public String getLoginIp()
    {
        return LoginIp;
    }

    public void setLoginIp(String loginIp)
    {
        LoginIp = loginIp;
    }

    public int getStatus()
    {
        return Status;
    }

    public void setStatus(int status)
    {
        Status = status;
    }

    public int getParentId()
    {
        return ParentId;
    }

    public void setParentId(int parentId)
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

    public int getLoginTime()
    {
        return LoginTime;
    }

    public void setLoginTime(int loginTime)
    {
        LoginTime = loginTime;
    }

    public String getMobile()
    {
        return Mobile;
    }

    public void setMobile(String mobile)
    {
        Mobile = mobile;
    }

    public String getNote()
    {
        return Note;
    }

    public void setNote(String note)
    {
        Note = note;
    }

    public boolean isNewRecord() {
        return id == null || id == 0;
    }
}
