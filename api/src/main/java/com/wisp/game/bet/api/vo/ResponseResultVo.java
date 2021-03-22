package com.wisp.game.bet.api.vo;

import org.springframework.http.HttpStatus;

public class ResponseResultVo<T>
{
    private int code;
    private T body;
    private String msg;

    public ResponseResultVo()
    {
        msg = "";
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public T getBody()
    {
        return body;
    }

    public void setBody(T body)
    {
        this.body = body;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public static ResponseResultVo success() {
        ResponseResultVo result = new ResponseResultVo();
        result.setCode(HttpStatus.OK.value());
        return result;
    }

    public static ResponseResultVo<Object> success(Object data) {
        ResponseResultVo result = new ResponseResultVo();
        result.setCode(HttpStatus.OK.value());
        result.setBody(data);
        return result;
    }

    public static ResponseResultVo<Object> success(Object data, String message) {
        ResponseResultVo result = new ResponseResultVo();
        result.setCode(HttpStatus.OK.value());
        result.setBody(data);
        result.setMsg(message);
        return result;
    }

    public static ResponseResultVo<Object> failure(int code) {
        ResponseResultVo result = new ResponseResultVo();
        result.setCode(code);
        return result;
    }

    public static ResponseResultVo<Object> failure(String message) {
        ResponseResultVo result = new ResponseResultVo();
        result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        result.setMsg(message);
        return result;
    }

    public static ResponseResultVo<Object> failure(Integer code, String message) {
        ResponseResultVo result = new ResponseResultVo();
        result.setCode(code);
        result.setMsg(message);
        return result;
    }
}