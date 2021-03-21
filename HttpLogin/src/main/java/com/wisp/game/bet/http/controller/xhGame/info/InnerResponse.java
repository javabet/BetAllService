package com.wisp.game.bet.http.controller.xhGame.info;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InnerResponse<T>
{
    @JsonProperty( value = "Code")
    private int Code;
    @JsonProperty( value = "Data")
    private T Data;

    public int getCode()
    {
        return Code;
    }

    public void setCode(int code)
    {
        Code = code;
    }

    public T getData()
    {
        return Data;
    }

    public void setData(T data)
    {
        Data = data;
    }
}
