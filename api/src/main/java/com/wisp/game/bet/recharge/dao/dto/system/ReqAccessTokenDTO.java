package com.wisp.game.bet.recharge.dao.dto.system;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ReqAccessTokenDTO
{
    @NotNull
    private String token;

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }
}
