package com.wisp.core.interceptor;




import java.lang.annotation.*;

/*
跳过验证问题
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface SkipAuthToken
{

}
