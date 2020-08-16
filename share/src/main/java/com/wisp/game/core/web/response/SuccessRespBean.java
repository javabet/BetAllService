package com.wisp.game.core.web.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class SuccessRespBean<T> implements Serializable {
    private static final long serialVersionUID = -5881964204263384782L;

    //spring boot RestController ResponseBody 输出json字段重命名
    //大概就是类字段或该字段所在的get和set其中一个被@JsonProperty标注了，序列化反序列化都统一了
    //或者是在getter上面直接增加JsonProperty

    //@JsonProperty(value = "Code" )
    private int Code;
    @JsonProperty(value = "Message",index = 1)
    private String Message;
    @JsonProperty(value = "Data",index = 2)
    public T Data;

    public SuccessRespBean() {
    }

    @JsonProperty("Code")
    //@JsonIgnore
    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    @JsonIgnore
    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    @JsonIgnore
    public T getData() {
        return Data;
    }

    public void setData(T data) {
        Data = data;
    }
}
