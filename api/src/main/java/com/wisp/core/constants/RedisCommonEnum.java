package com.wisp.core.constants;

public enum RedisCommonEnum
{
    AUTH_KEY_PREFIX("AUTH_KEY_PREFIX"),
    ;

    public String getKey()
    {
        return code;
    }

    private String code;

    RedisCommonEnum(String code)
    {
        this.code = code;
    }
}
