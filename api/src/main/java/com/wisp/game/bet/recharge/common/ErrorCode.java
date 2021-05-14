package com.wisp.game.bet.recharge.common;

public enum ErrorCode
{
    ERR_PARAM(1001,"参数错误"),
    ERR_DB_DATA(1002,"无此数据"),
    ERR_DB_INSERT(1003,"查放数据失败"),
    ERR_DB_UPDATE(1004,"更新数据失败"),
    ERR_DB_SELECT(1005,"查询数据失败"),
    ERR_PARAM_FIELD(1006,"查询数据失败"),
    ERR_NO_DATA(1007,"无此数据"),

    ;

    public int getCode()
    {
        return code;
    }

    public String getMsg()
    {
        return msg;
    }

    private int code;
    private String msg;

    ErrorCode(int code, String msg)
    {
        this.code = code;
        this.msg = msg;
    }
}
