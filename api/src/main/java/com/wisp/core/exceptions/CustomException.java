package com.wisp.core.exceptions;

public class CustomException extends RuntimeException
{
    private int code;

    public CustomException(int code)
    {
        this.code = code;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }
}
