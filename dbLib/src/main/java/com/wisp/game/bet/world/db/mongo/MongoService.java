package com.wisp.game.bet.world.db.mongo;


import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface MongoService {
    String value() default "";
}
