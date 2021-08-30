package com.wisp.game.bet.recharge.beanConfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@ConfigurationProperties("common")
public class YmlConfig
{
    private String passwordSalt;

    private int TokenMaxExpSecond = 86400;

    private int SystemAdminId = -1;

    private List<Integer> SpecialPermissions;

    public String getPasswordSalt()
    {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt)
    {
        this.passwordSalt = passwordSalt;
    }

    public int getTokenMaxExpSecond()
    {
        return TokenMaxExpSecond;
    }

    public void setTokenMaxExpSecond(int tokenMaxExpSecond)
    {
        TokenMaxExpSecond = tokenMaxExpSecond;
    }

    public int getSystemAdminId()
    {
        return SystemAdminId;
    }

    public void setSystemAdminId(int systemAdminId)
    {
        SystemAdminId = systemAdminId;
    }


    public List<Integer> getSpecialPermissions()
    {
        return SpecialPermissions;
    }

    public void setSpecialPermissions(List<Integer> specialPermissions)
    {
        SpecialPermissions = specialPermissions;
    }
}
