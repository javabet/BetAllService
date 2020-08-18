package com.wisp.game.core.web.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorRespBean<T> {
    private int code;
    private String message;
    private T data;

    @JsonProperty("Code")
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @JsonProperty("Message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("Data")
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}