package com.wisp.game.bet.http.controller.xhGame.info;

import com.fasterxml.jackson.annotation.JsonProperty;

public class XhResponse<T>
{
    @JsonProperty( value = "Code")
    private int Code;
    @JsonProperty( value = "Msg")
    private InnerResponse<T> Msg;

    public int getCode()
    {
        return Code;
    }

    public void setCode(int code)
    {
        Code = code;
    }

    public InnerResponse<T> getMsg()
    {
        return Msg;
    }

    public void setMsg(InnerResponse<T> msg)
    {
        Msg = msg;
    }
}
