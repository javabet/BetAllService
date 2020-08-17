package com.wisp.game.bet.share.netty;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Component
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface IRequest {

    @AliasFor("cmd")
    int value() default -1;

    @AliasFor("value")
    int cmd() default -1;
}
