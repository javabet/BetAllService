package com.wisp.game.bet.recharge.dao.dto.system;

public class ReqLoginDTO
{
    private String Username;

    private String Password;

    private String Captcha;

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

    public String getCaptcha()
    {
        return Captcha;
    }

    public void setCaptcha(String captcha)
    {
        Captcha = captcha;
    }
}
