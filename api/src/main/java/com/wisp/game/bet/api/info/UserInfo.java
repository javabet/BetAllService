package com.wisp.game.bet.api.info;

import java.util.Date;

public class UserInfo
{
    private String username;

    private Integer age;

    private Integer phone;

    private String email;

    private Date date;

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public Integer getAge()
    {
        return age;
    }

    public void setAge(Integer age)
    {
        this.age = age;
    }

    public Integer getPhone()
    {
        return phone;
    }

    public void setPhone(Integer phone)
    {
        this.phone = phone;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }


    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }
}
