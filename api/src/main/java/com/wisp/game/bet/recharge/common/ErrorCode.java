package com.wisp.game.bet.recharge.common;

import com.wisp.core.web.base.BaseErrorCode;

public enum ErrorCode implements BaseErrorCode
{
    ERR_PARAM(1001,"参数错误"),
    ERR_DB_DATA(1002,"无此数据"),
    ERR_DB_INSERT(1003,"查放数据失败"),
    ERR_DB_UPDATE(1004,"更新数据失败"),
    ERR_DB_SELECT(1005,"查询数据失败"),
    ERR_PARAM_FIELD(1006,"查询数据失败"),
    ERR_NO_DATA(1007,"无此数据"),
    ERR_USER_NAME(1008,"玩家已经存在"),
    ERR_ACCOUNT_ALREADY_LOGIN(1009,"账号已经登陆"),
    ERR_ACCOUNT_PWD_ERR(10010,"账号或密码错误"),
    ERR_ACCESS_TOKEN(10011,"登陆失败"),
    ERR_TOKEN(10012,"Token无效"),
    ERR_RIGHT(10013,"无权限操作"),
    ERR_NOT_SELF_ROLE(10014,"不可修改非自建角色"),
    ERR_LOGIN_FIRST(10015,"请先登陆"),
    ERR_TIME_OUT(10016,"时间过期"),
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
