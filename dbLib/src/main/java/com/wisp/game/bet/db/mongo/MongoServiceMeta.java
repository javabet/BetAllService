package com.wisp.game.bet.db.mongo;


import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface MongoServiceMeta {
    String value() default "";
}
