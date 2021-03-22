package com.wisp.game.bet.api.service;

import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@RestController
@ResponseResult
public @interface BaseControllerAnnotation
{
    //https://developer.aliyun.com/article/779341
}
